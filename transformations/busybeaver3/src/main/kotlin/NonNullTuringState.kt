package de.joshuagleitze.transformationnetwork.transformations.busybeaver3

import de.joshuagleitze.transformationnetwork.models.turingmachine.TuringState
import kotlin.math.max

data class NonNullTuringState(val timestamp: Int, val band: String, val bandPosition: Int) {
    fun ensureCanMoveRight(newChar: Char = ' ') = copy(
        band = if (this.bandPosition == this.band.length - 1) this.band + newChar else this.band
    )

    fun ensureCanMoveLeft(newChar: Char = ' ') = copy(
        band = if (this.bandPosition == 0) newChar + this.band else this.band,
        bandPosition = max(0, this.bandPosition)
    )

    fun fixBandRange(newChar: Char) = when {
        bandPosition < 0 -> copy(band = "$newChar".repeat(0 - bandPosition) + band, bandPosition = 0)
        bandPosition > band.lastIndex -> copy(band = band + "$newChar".repeat(bandPosition - band.lastIndex))
        else -> this
    }

    val currentChar get() = band[bandPosition]

    fun isLeftOf(otherState: NonNullTuringState, distance: Int) =
        if (otherState.bandPosition - distance < 0) {
            this.bandPosition == 0 && this.band.substring(distance - otherState.bandPosition) == otherState.band
        } else this.bandPosition == otherState.bandPosition - distance
}

fun getNotNull(state: TuringState): NonNullTuringState? =
    state.timestamp?.let { timestamp ->
        state.band?.let { band ->
            state.bandPosition?.let { bandPosition ->
                NonNullTuringState(timestamp, band, bandPosition)
            }
        }
    }

fun TuringState.setTo(state: NonNullTuringState) {
    timestamp = state.timestamp
    band = state.band
    bandPosition = state.bandPosition
}
