package com.mygdx.platformer.engine

import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.engine.Sensor

/**
 * Created by feresr on 13/11/17.
 */
class Entity(val position: Vector2, val width: Int, val height: Int, val sensors: Array<Sensor> = emptyArray()) {

    var gravity = 1f
    var isActive = false
    val velocity: Vector2 = Vector2(0f, 0f)
    var userData: Any? = null

    var onUpdate: ((Float) -> Unit)? = null

    var onCollisionLeft: (() -> Unit)? = null
    var onCollisionRight: (() -> Unit)? = null
    var onCollisionBottom: (() -> Unit)? = null
    var onCollisionTop: (() -> Unit)? = null
}