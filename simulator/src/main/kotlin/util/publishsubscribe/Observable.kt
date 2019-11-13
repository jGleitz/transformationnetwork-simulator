package de.joshuagleitze.transformationnetwork.simulator.util.publishsubscribe

interface Observable<Data> {
    val current: Data
    fun subscribe(subscriber: (Data) -> Unit)
    fun unsubscribe(subscriber: (Data) -> Unit)
}