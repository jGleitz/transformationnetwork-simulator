package de.joshuagleitze.transformationnetwork.changeablemodel

import de.joshuagleitze.transformationnetwork.changemetamodel.ChangeSet

interface ChangeRecording {
    fun getLastChanges(): ChangeSet
    fun resetLastChanges()
}