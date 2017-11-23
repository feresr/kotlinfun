package com.mygdx.platformer.engine

import com.badlogic.gdx.math.Rectangle


/**
 * Created by feresr on 16/11/17.
 */
data class Sensor(val rectangle: Rectangle, val f: (Entity) -> Unit)