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

    var onCollision: ((Entity?) -> Unit)? = null
    var onCollisionLeft: ((Entity?) -> Unit)? = null
    var onCollisionRight: ((Entity?) -> Unit)? = null
    var onCollisionBottom: ((Entity?) -> Unit)? = null
    var onCollisionTop: ((Entity?) -> Unit)? = null
}