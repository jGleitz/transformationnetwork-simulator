package de.joshuagleitze.transformationnetwork.changerecording

import de.joshuagleitze.transformationnetwork.changemetamodel.AdditionChange
import de.joshuagleitze.transformationnetwork.changemetamodel.AttributeSetChange
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.ChangeSet
import de.joshuagleitze.transformationnetwork.changemetamodel.changeset.changeSetOf
import de.joshuagleitze.transformationnetwork.metametamodel.MetaAttribute
import de.joshuagleitze.transformationnetwork.metametamodel.Model
import de.joshuagleitze.transformationnetwork.metametamodel.ModelObject
import de.joshuagleitze.transformationnetwork.metametamodel.byIdentity

fun Model.changesForAdding(modelObject: ModelObject): ChangeSet {
    return changeSetOf(
        AdditionChange(this.identity, modelObject.metaclass, modelObject.identity),
        *modelObject.metaclass.attributes.map { attribute ->
            @Suppress("UNCHECKED_CAST")
            AttributeSetChange(
                this.identity,
                byIdentity(modelObject.identity),
                attribute as MetaAttribute<Any>,
                modelObject[attribute]
            )
        }.toTypedArray()
    )
}