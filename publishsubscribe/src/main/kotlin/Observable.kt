package de.joshuagleitze.transformationnetwork.publishsubscribe

interface Observable<out Data> {
    val last: Data?
    fun subscribe(subscriber: (Data) -> Unit)
    fun unsubscribe(subscriber: (Data) -> Unit)
}