package com.mygdx.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.platformer.enemies.Enemy
import com.mygdx.platformer.enemies.Plant
import com.mygdx.platformer.engine.Entity
import com.mygdx.platformer.engine.World
import com.mygdx.platformer.items.Box
import com.mygdx.platformer.items.Coin
import com.mygdx.platformer.items.Door
import com.mygdx.platformer.items.Lapse

/**
 * Created by feresr on 11/11/17.
 */
class PlayScreen(private val game: Platformer, private val mapName: String) : Screen {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Platformer.V_WIDTH, Platformer.V_HEIGHT, camera)

    private var currentLevel = mapName
    private var isHeroDead = false

    private val map = TmxMapLoader().load(mapName)
    private val renderer = OrthogonalTiledMapRenderer(map, game.batch)

    private val world: World = World(map.layers[1] as TiledMapTileLayer, .4f)

    private val hero: Hero = Hero(128f, 256f, { world.addEntity(it) }, this::onDoorSelected, { isHeroDead = true })

    private val entities: Array<Entity> = Array()
    private val shader: ShaderProgram = ShaderProgram(Gdx.files.internal("shaders/vignette.vsh"), Gdx.files.internal("shaders/vignette.fsh"))

    override fun show() {
        createWorldObjects()
        camera.position.y = viewport.worldHeight / 2f
        camera.zoom = 1f
        ShaderProgram.pedantic = false
        renderer.batch.shader = shader
    }

    private fun removeEntity(entity: Entity) {
        world.removeEntity(entity)
        if (renderer.batch.shader == shader) {
            renderer.batch.shader = null
        } else {
            renderer.batch.shader = shader
        }
        entities.removeValue(entity, true)
    }

    private fun createWorldObjects() {

        //entities.add(ForgetPlatform(200f, 94f, 200, 100).body)
        //entities.add(Immortal(200f, 94f, { removeEntity(it) }).body)

        for (o in map.layers[8].objects.getByType(RectangleMapObject::class.java)) {
            entities.add(Lapse(o.rectangle.x, o.rectangle.y, o.rectangle.width, o.rectangle.height).body)
        }

        for (o in map.layers[7].objects.getByType(RectangleMapObject::class.java)) {
            entities.add(Coin(o.rectangle.x, o.rectangle.y, { removeEntity(it) }).body)
        }

        //Enemies
        for (o in map.layers[3].objects.getByType(RectangleMapObject::class.java)) {
            entities.add(Enemy(o.rectangle.x, o.rectangle.y, this::removeEntity).body)
        }

        //Plantsr
        for (o in map.layers[4].objects.getByType(RectangleMapObject::class.java)) {
            entities.add(Plant(o.rectangle.x + o.rectangle.width / 2, o.rectangle.y).body)
        }

        //Doors
        for (o in map.layers[5].objects.getByType(RectangleMapObject::class.java)) {
            entities.add(Door(o.rectangle.x, o.rectangle.y, o.rectangle.width.toInt(), o.rectangle.height.toInt(), o.properties["level"].toString()).body)
        }

        //Boxes
        for (o in map.layers[6].objects.getByType(RectangleMapObject::class.java)) {
            entities.add(Box(o.rectangle.x, o.rectangle.y, o.rectangle.width.toInt(), o.rectangle.height.toInt()).body)
        }
    }

    private fun onDoorSelected(door: Door) {
        currentLevel = door.level
    }

    private fun update(delta: Float) {

        entities.forEach {
            if (!it.isInWorld && it.position.x - hero.body.position.x < (Platformer.V_WIDTH / 2) + Platformer.AWAKE_THRESHOLD) {
                it.isInWorld = true
                world.addEntity(it)
            }
            it.onUpdate?.invoke(delta)
        }

        hero.body.onUpdate?.invoke(delta)
        if (hero.body.position.y < 0f) {
            isHeroDead = true
        }

        world.step(delta)
    }

    override fun render(delta: Float) {
        if (currentLevel != mapName || isHeroDead) {
            if (currentLevel == mapName) {
                currentLevel = if (currentLevel == "map.tmx") "map2.tmx" else "map.tmx"
            }
            game.screen = PlayScreen(game, currentLevel)
            dispose()
            return
        }

        update(delta)

        Gdx.gl.glClearColor(.26f, .82f, .98f, 1f)

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        //camera.position.x = hero.body.position.x
        //camera.position.y = hero.body.position.y + 20

        val lerpX = 20f
        val lerpY = 2f
        val cameraYOffset = 20
        camera.position.x += (hero.body.position.x - camera.position.x) * lerpX * delta
        //camera.position.y = 100f
        camera.position.y += (hero.body.position.y - camera.position.y + cameraYOffset) * lerpY * delta

        camera.update()
        game.batch.projectionMatrix = camera.combined
        world.render(camera.combined)

        renderer.setView(camera)
        renderer.render()

        game.batch.begin()

        if (hero.isInsideLapse()) {
            renderer.batch.shader = shader
        } else {
            renderer.batch.shader = null
        }
        for (entity in entities) {
            //EVERYTHING MUST HAVE A TEXTURE TO BE DRAWN! ELSE NullPointerException
            //(entity.userData as? Sprite)?.draw(game.batch)

            (entity.userData as? Coin)?.let {
                it.draw(game.batch)
            }
            (entity.userData as? Lapse)?.let {
                it.draw(game.batch)
            }
        }

        hero.draw(game.batch)

        game.batch.end()

    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        shader.begin();
        shader.setUniformf("u_resolution", width.toFloat(), height.toFloat())
        shader.end();
    }

    override fun dispose() {
        map.dispose()
        renderer.dispose()
        hero.dispose()
    }
}


