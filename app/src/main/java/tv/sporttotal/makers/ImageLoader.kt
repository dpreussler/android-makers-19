package tv.sporttotal.makers

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

interface ImageLoader {
    suspend fun loadImage(image: String): Image
}


class SimpleImageLoader : ImageLoader {
    override suspend fun loadImage(image: String): Image {
        return Image(image)
    }

}













class ImageFilter(
    val imageLoader: ImageLoader = SimpleImageLoader(),
    val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers()) {









    suspend fun loadAndCombine(image1: String, image2: String): Image =
        coroutineScope {
            val deferred1 = async { imageLoader.loadImage(image1) }
            val deferred2 = async { imageLoader.loadImage(image2) }
            combineImages(deferred1.await(), deferred2.await())
        }













    private suspend fun combineImages(image1: Image, image2: Image) : Image {
        delay(3000)
        return Image("${image1.name}:${image2.name}")
    }





















    suspend fun loadAndSwitchAndCombine(name1: String, name2: String): Image =
        coroutineScope {
            val deferred1 = async { imageLoader.loadImage(name1) }
            val deferred2 = async { imageLoader.loadImage(name2) }
            val images = awaitAll(deferred1, deferred2)

            withContext(dispatchers.io) {
                combineImages(images.first(), images.last())
            }
        }
}
