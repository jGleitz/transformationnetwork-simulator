package de.joshuagleitze.transformationnetwork.simulator

interface Observable<Data> {
    val current: Data
    fun subscribe(subscriber: (Data) -> Unit)
    fun unsubscribe(subscriber: (Data) -> Unit)
}