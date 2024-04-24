package ravenweave.client.hook.finder

import ravenweave.client.util.MappingsWorkAroundFinder

class `Finder$EntityRenderer$getNightVisionBrightness` : MappingsWorkAroundFinder(
    ID,
    "net/minecraft/client/renderer/EntityRenderer",
    "updateLightmap",
    "updateCameraAndRender"
) {
    companion object {
        const val ID = "EntityRenderer\$getNightVisionBrightness"
    }
}

class `Finder$Entity$callResetPositionToBB` : MappingsWorkAroundFinder(
    ID,
    "net/minecraft/entity/Entity",
    "moveEntity",
    "getSwimSound"
) {
    companion object {
        const val ID = "Entity\$callResetPositionToBB"
    }
}