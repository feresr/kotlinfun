package com.mygdx.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.platformer.enemies.Enemy
import com.mygdx.platformer.enemies.Plant
import com.mygdx.platformer.engine.World


/**
 * Created by feresr on 11/11/17.
 */
class PlayScreen(private val game: Platformer, val mapName: String) : Screen {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Platformer.V_WIDTH, Platformer.V_HEIGHT, camera)

    private var currentLevel = mapName

    private val map = TmxMapLoader().load(mapName)
    private val renderer = OrthogonalTiledMapRenderer(map, 1f)

    private val world: World = World(map.layers[1] as TiledMapTileLayer)

    private val hero: Hero = Hero(64f, 32f, { world.addEntity(it) }, this::onDoorSelected, this::onHeroDied)

    private val enemies: Array<Enemy> = Array()

    override fun show() {
        createWorldObjects()
        camera.position.y = viewport.worldHeight / 2f
    }

    private fun createWorldObjects() {

        for (o in map.layers[4].objects.getByType(RectangleMapObject::class.java)) {
            Plant(o.rectangle.x + o.rectangle.width / 2, o.rectangle.y, { world.addEntity(it) })
        }
        for (o in map.layers[5].objects.getByType(RectangleMapObject::class.java)) {
            Door(o.rectangle.x, o.rectangle.y, { world.addEntity(it) }, o.properties["level"].toString())
        }

        for (o in map.layers[3].objects.getByType(RectangleMapObject::class.java)) {
            enemies.add(Enemy(o.rectangle.x, o.rectangle.y, { world.addEntity(it) }))
        }
    }

    private fun onDoorSelected(door: Door) {
        currentLevel = door.level
    }

    private fun onHeroDied() {
        currentLevel = if (currentLevel =="map.tmx") "map2.tmx" else "map.tmx"
    }


    override fun render(delta: Float) {
        if (currentLevel != mapName) {
            game.screen = PlayScreen(game, currentLevel)
            dispose()
            return
        }

        update(delta)

        Gdx.gl.glClearColor(0f, .54f, .77f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        //camera.position.x = hero.body.position.x
        //camera.position.y = hero.body.position.y + 80

        val lerpX = 5f
        val lerpY = 3f
        val cameraYOffset = 30
        camera.position.x += (hero.body.position.x - camera.position.x) * lerpX * delta
        camera.position.y += (hero.body.position.y - camera.position.y + cameraYOffset) * lerpY * delta

        camera.update()
        game.batch.projectionMatrix = camera.combined
        world.render(camera.combined)

        //shapeRenderer.projectionMatrix =

        renderer.setView(camera)
        renderer.render()
    }

    private fun update(delta: Float) {
        world.step(delta)
        for (enemy in enemies) {
            if (enemy.position.x - hero.body.position.x < (Platformer.V_WIDTH / 2) + Platformer.ENEMY_WAKE_THRESHOLD) {
                if (!enemy.isDead) {
                    enemy.setActive(true)
                }
            }
            //if (enemy.isDead) {
            //world.destroyBody(enemy.body)
            //    enemies.removeValue(enemy, true)
            //} else {
            //enemy.update(delta)
            //}
        }
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        map.dispose()
        renderer.dispose()
        hero.dispose()
    }
}


