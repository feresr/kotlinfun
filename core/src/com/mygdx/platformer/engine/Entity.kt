package com.mygdx.platformer.engine

import com.badlogic.gdx.math.Vector2

class Entity(val position: Vector2,
             val width: Int, val height: Int,
             val sensors: Array<Sensor> = emptyArray()) {

    var gravity = 1f
    var isInWorld = false

    var isVisible = true
    val velocity: Vector2 = Vector2()
    var userData: Any? = null

    var onUpdate: ((Float) -> Unit)? = null

    var onCollision: ((Any?) -> Unit)? = null
    var onCollisionLeft: ((Any?) -> Unit)? = null
    var onCollisionRight: ((Any?) -> Unit)? = null
    var onCollisionBottom: ((Any?) -> Unit)? = null
    var onCollisionTop: ((Any?) -> Unit)? = null
}