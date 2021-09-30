package it.uninsubria.dmx_calculator.firebase

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.uninsubria.dmx_calculator.enumerator.FixtureTypeEnum
import it.uninsubria.dmx_calculator.enumerator.LampTypeEnum
import it.uninsubria.dmx_calculator.models.Fixture

private const val TAG = "Firebase_Firestore"

class Firestore {
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

    fun getAllFixture(callback: (ArrayList<Fixture>) -> Unit) {
        db.collection("fixture").orderBy("manufacturer").get()
            .addOnCompleteListener { resultTask ->
                val fixtureList: ArrayList<Fixture> = arrayListOf()
                if(resultTask.isSuccessful) {
                    for(qs in resultTask.result!!) {
                        val currentFixture = Fixture(qs["name"].toString(),                         // nome
                                                     qs["manufacturer"].toString(),                 // produttore
                                                     setFixtureMode(qs),                            // modalita DMX
                                                     qs["powerConsumption"].toString().toInt(),     // consumo elettrico
                                                     setFixtureType(qs),                            // tipo di Fixture (enum)
                                                     setLampType(qs),                               // tipo di lampada (enum)
                                                     qs["minZoom"].toString().toFloat(),            // zoom minimo
                                                     qs["maxZoom"].toString().toFloat())            // zoom massimo
                        // Imposto riferimento firestore
                        currentFixture.setReference(qs.id)
                        // aggiungo la fixture alla lista
                        fixtureList.add(currentFixture)
                    }
                    callback(fixtureList)
                } else {
                    Log.e(TAG, "Firestore error")
                    callback(fixtureList)
                }
            }
    }

    fun getAllManufacturer(callback: (List<String>) -> Unit) {
        val manufacturerList: MutableSet<String> = mutableSetOf()
        getAllFixture { fixtureList ->
            for(fixture in fixtureList) {
                manufacturerList.add(fixture.manufacturer)
            }
            callback(manufacturerList.sorted())
        }
    }

    fun getFixture(fixtureRef: String, callback: (Fixture) -> Unit) {
        db.collection("fixture").document(fixtureRef).get().addOnCompleteListener { task ->
            if(task.isSuccessful and (task.result != null)) {
                val result = task.result!!
                val fixture = Fixture(result["name"].toString(),                         // nome
                                      result["manufacturer"].toString(),                 // produttore
                                      setFixtureMode(result),                            // modalita DMX
                                      result["powerConsumption"].toString().toInt(),     // consumo elettrico
                                      setFixtureType(result),                            // tipo di Fixture (enum)
                                      setLampType(result),                               // tipo di lampada (enum)
                                      result["minZoom"].toString().toFloat(),            // zoom minimo
                                      result["maxZoom"].toString().toFloat())            // zoom massimo
                // Imposto riferimento firestore
                task.result?.let { fixture.setReference(it.id) }
                callback(fixture)
            }

        }
    }

    private fun setFixtureMode(qs: DocumentSnapshot): HashMap<String, Int> {
        val mode: HashMap<String, Int> = hashMapOf()
        // Imposto modalita' di funzionamento
        for((k, v) in qs["mode"] as HashMap<*,*>) {
            mode[k.toString()] = v.toString().toInt()       // k mode, v footprint
        }
        return mode
    }

    private fun setFixtureType(qs: DocumentSnapshot): ArrayList<FixtureTypeEnum> {
        val fixtureType: ArrayList<FixtureTypeEnum> = arrayListOf()
        for(fixType in qs["projectorType"] as ArrayList<*>) {
            when(fixType) {
                "SPOT" -> fixtureType.add(FixtureTypeEnum.SPOT)
                "WASH" -> fixtureType.add(FixtureTypeEnum.WASH)
                "BEAM" -> fixtureType.add(FixtureTypeEnum.BEAM)
                "PROFILE" -> fixtureType.add(FixtureTypeEnum.PROFILE)
                "UNKNOWN" -> fixtureType.add(FixtureTypeEnum.UNKNOWN)
            }
        }
        return fixtureType
    }

    private fun setLampType(qs: DocumentSnapshot): LampTypeEnum {
        when(qs["lampType"].toString()) {
            "DISCHARGE" -> return LampTypeEnum.DISCHARGE
            "LED" -> return LampTypeEnum.LED
            "LASER" -> return LampTypeEnum.LASER
            "INCANDESCENT" -> return LampTypeEnum.INCANDESCENT
            "HALOGEN" -> return LampTypeEnum.HALOGEN
            "FLUORESCENT" -> return LampTypeEnum.FLUORESCENT
            "UNKNOWN" -> return LampTypeEnum.UNKNOWN
        }
        return LampTypeEnum.UNKNOWN
    }

    fun getFixtureByManufacturer(fixMmanu: String, callback: (List<String>) -> Unit) {
        val fixtureNameList: MutableSet<String> = mutableSetOf()
        getAllFixture { fixtureList ->
            for(fixture in fixtureList) {
                if(fixture.manufacturer == fixMmanu) {
                    fixtureNameList.add(fixture.name)
                }
            }
            callback(fixtureNameList.sorted())
        }
    }

    fun getModeByFixtureName(fixName: String, callback: (List<String>) -> Unit) {
        val modeList: MutableSet<String> = mutableSetOf()
        getAllFixture { fixtureList ->
            for(fixture in fixtureList) {
                if(fixture.name == fixName) {
                    for(singleMode in fixture.mode) {
                        modeList.add("${singleMode.key} -> ${singleMode.value}")
                    }
                }
            }
            callback(modeList.sorted())
        }
    }

    fun getFixtureRef(fixMmanu: String, fixName: String, callback: (String) -> Unit) {
        getAllFixture { fixtureList ->
            for(fixture in fixtureList) {
                if(fixture.manufacturer == fixMmanu && fixture.name == fixName) {
                    callback(fixture.getReference())
                }
            }
        }
        callback("")
    }
}
