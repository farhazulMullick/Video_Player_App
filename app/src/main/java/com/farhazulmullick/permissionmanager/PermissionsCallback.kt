package com.farhazulmullick.permissionmanager

interface PermissionsCallback {
    fun onGranted()
    fun onDenied()
}