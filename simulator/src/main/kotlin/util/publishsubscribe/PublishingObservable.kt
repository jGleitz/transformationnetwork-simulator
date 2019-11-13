package de.joshuagleitze.transformationnetwork.simulator.util.publishsubscribe

import kotlin.js.Promise

class PublishingObservable<Data>(override var current: Data) : Observable<Data> {
    private val subscribers = HashSet<(Data) -> Unit>()

    override fun subscribe(subscriber: (Data) -> Unit) {
        check(subscribers.add(subscriber)) { "$subscriber was already subscribed!" }
    }

    override fun unsubscribe(subscriber: (Data) -> Unit) {
        check(subscribers.remove(subscriber)) { "$subscriber was not subscribed in the first place!" }
    }

    fun publishIfChanged(data: Data) {
        if (data != current) {
            current = data
            subscribers.forEach { subscriber ->
                Promise.resolve(Unit).then {
                    subscriber(data)
                }
            }
        }
    }
}