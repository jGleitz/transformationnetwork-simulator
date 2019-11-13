package de.joshuagleitze.transformationnetwork.changeablemodel

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditiveChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.additiveCopy


fun planChanges(models: List<ChangeRecordingModel>, vararg changers: () -> Unit): List<ChangeSet> {
    check(models.isNotEmpty()) { "no models passed!" }
    val resultList = ArrayList<ChangeSet>()
    for (model in models) model.resetLastChanges()
    for (changer in changers) {
        changer()
        var result: AdditiveChangeSet? = null
        for (model in models) {
            if (result == null) result = model.getLastChanges().additiveCopy()
            else result.addAll(model.getLastChanges())
            model.resetLastChanges()
        }
        resultList.add(result!!)
    }
    resultList.reversed().flatten().forEach { it.revert() }
    return resultList
}