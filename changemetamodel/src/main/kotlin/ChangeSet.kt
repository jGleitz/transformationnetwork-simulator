package de.joshuagleitze.transformationnetwork.changemetamodel

import de.joshuagleitze.transformationnetwork.metametamodel.Model

interface ChangeSet : Set<ModelChange> {
    val targetModel: Model
    val deletions: Collection<DeletionChange>
    val additions: Collection<AdditionChange>
    val modifications: Collection<AttributeChange>
}