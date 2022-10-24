package com.famas.eprayogshala.util.experiments

import com.famas.eprayogshala.R

sealed class Experiment(val name: String, val image: Int) {
    object AcidBase: Experiment(name = "Acid Base Titration", image = R.drawable.acidbasetitrationbanner)
    object Conductometric1: Experiment(name = "Conductometric Titration of Strong Acid with strong Base", image = R.drawable.conductometricsasb)
    object Conductometric2: Experiment(name = "Conductometric Titration of Weak Base with strong Acid", image = R.drawable.conductometricwasb)
    object Potentiometric: Experiment(name = "Potentiometric Titration", image = R.drawable.potentiometric)
    object Colorimetric: Experiment(name = "Colorimetric Titration", image = R.drawable.colorimetric)
    object PHmetric: Experiment(name = "PHmetric Titration", image = R.drawable.phmetric)
    object HardnessOfWater: Experiment(name = "Hardness of water", image = R.drawable.acidbasetitrationbanner)
}
