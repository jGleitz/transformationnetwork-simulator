package metamodelling.factory

import de.joshuagleitze.transformationnetwork.metamodelling.Metamodel
import de.joshuagleitze.transformationnetwork.metamodelling.Model
import de.joshuagleitze.transformationnetwork.metamodelling.ModelObject

fun Metamodel.model(name: String, vararg objects: ModelObject): Model {
    val metamodel = this
    objects.forEach {
        check(metamodel.classes.contains(it.metaclass)) { "$it’s metaclass ${it.metaclass} does not belong to $metamodel!" }
    }
    return ModelImpl(metamodel, name, objects.toList())
}

private class ModelImpl(
    override val metamodel: Metamodel,
    override val name: String,
    override val objects: List<ModelObject>
) : Model