package com.famas.eprayogshala

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy


class EprayogshalaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
    }
}