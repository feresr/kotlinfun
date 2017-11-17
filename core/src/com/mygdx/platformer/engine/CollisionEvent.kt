package com.mygdx.platformer.engine

/**
 * Created by feresr on 14/11/17.
 */
data class CollisionEvent(val element: Any?, val action: (Any?) -> Unit)