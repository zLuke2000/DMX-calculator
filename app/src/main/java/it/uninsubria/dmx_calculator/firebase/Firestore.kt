package it.uninsubria.dmx_calculator.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.uninsubria.dmx_calculator.enumerator.FixtureTypeEnum
import it.uninsubria.dmx_calculator.enumerator.LampTypeEnum
import it.uninsubria.dmx_calculator.models.Fixture

class Firestore {

    private val TAG = "Firestore"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun addFixture(fixture: Fixture, callback: (Boolean) -> Unit) {
        db.collection("fixture")
            .add(fixture)
            .addOnSuccessListener { result ->
                Log.d(TAG, "[SUCCESSO] fixture aggiunta con ID: ${result.id}")
                callback(true)
            }
            .addOnFailureListener { error ->
                Log.w(TAG, "[ERRORE] caricamento fixture su DB: $error")
                callback(false)
            }
    }

    fun getAllFixture(callback: (List<Fixture>?) -> Unit) {
        db.collection("fixture")
            .addSnapshotListener { result, error ->
                if (error == null && result != null) {
                    val fixtureList: ArrayList<Fixture> = arrayListOf()
                    for(qs in result) {
                        val currentFixture = Fixture(qs["name"].toString(),                             // nome
                                                     qs["manufacturer"].toString(),                     // produttore
                                                     qs["mode"].toString(),                             // modalita DMX
                                                     qs["footprint"].toString().toInt(),                // numero canali DMX
                                                     qs["powerConsumption"].toString().toInt(),         // consumo elettrico
                                                     qs["projectorType"] as List<FixtureTypeEnum>,      // tipo di Fixture (enum)
                                                     qs["lampType"] as LampTypeEnum)                    // tipo di lampada (enum)
                        currentFixture.setID(qs["id"] as Int)
                        fixtureList.add(currentFixture)
                    }
                    callback(fixtureList)
                } else if (error != null) {
                    Log.e(TAG, "Firestore error: " + error.message.toString())
                    callback(null)
                }
            }
    }
}