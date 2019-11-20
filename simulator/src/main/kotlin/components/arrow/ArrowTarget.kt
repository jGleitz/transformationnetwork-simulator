package de.joshuagleitze.transformationnetwork.simulator.components.arrow

import de.joshuagleitze.transformationnetwork.publishsubscribe.Observable
import de.joshuagleitze.transformationnetwork.publishsubscribe.PublishingObservable
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.ArrowTargetData
import de.joshuagleitze.transformationnetwork.simulator.data.arrow.DefaultArrowTargetData
import de.joshuagleitze.transformationnetwork.simulator.util.externals.ResizeObserver
import de.joshuagleitze.transformationnetwork.simulator.util.geometry.Coordinate
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RReadableRef
import react.RState
import kotlin.browser.window

interface ArrowTargetProps : RProps {
    var targetRef: RReadableRef<HTMLElement>
}

interface ArrowTarget {
    val data: Observable<ArrowTargetData>
}

private class ArrowTargetComponent private constructor() : RComponent<ArrowTargetProps, RState>(), ArrowTarget {
    override var data = PublishingObservable<ArrowTargetData>(null)
    private lateinit var targetContainer: HTMLElement
    private lateinit var resizeObserver: ResizeObserver

    override fun RBuilder.render() {
        props.children()
    }

    @Suppress("UNUSED_PARAMETER") // used to make a method reference for event listeners possible
    private fun publishData(causingEvent: Event? = null) {
        val outerRect = targetContainer.getBoundingClientRect()
        val bottomLeft = Coordinate(outerRect.left, outerRect.bottom)
        val topRight = Coordinate(outerRect.right, outerRect.top)
        this.data.publishIfChanged(DefaultArrowTargetData(bottomLeft, topRight))
    }

    override fun componentDidMount() {
        targetContainer = props.targetRef.current!!
        resizeObserver = ResizeObserver { _, _ -> publishData() }
        resizeObserver.observe(targetContainer)
        publishData()
        window.addEventListener("resize", this::publishData)
    }

    override fun componentWillUnmount() {
        window.removeEventListener("resize", this::publishData)
        resizeObserver.disconnect()
    }
}

fun RBuilder.ArrowTarget(targetRef: RReadableRef<HTMLElement>, handler: RHandler<ArrowTargetProps>) =
    child(ArrowTargetComponent::class) {
        attrs.targetRef = targetRef
        handler()
    }