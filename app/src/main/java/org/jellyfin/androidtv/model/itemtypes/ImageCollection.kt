package org.jellyfin.androidtv.model.itemtypes

import android.content.Context
import android.util.Log
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jellyfin.androidtv.TvApp
import org.jellyfin.apiclient.model.dto.BaseItemDto
import org.jellyfin.apiclient.model.dto.ImageOptions
import org.jellyfin.apiclient.model.entities.ImageType

class ImageCollection(original: BaseItemDto) {
	val primary = original.imageTags[ImageType.Primary]?.let { Image(original.id, ImageType.Primary, it) }
	val logo = original.imageTags[ImageType.Logo]?.let { Image(original.id, ImageType.Logo, it) }
	val backdrops = original.backdropImageTags.map { Image(original.id, ImageType.Backdrop, it) }.toList()

	class Image(private val itemId: String, private val type: ImageType, private val tag: String, private val index: Int? = null) {

		val url: String by lazy {
			TvApp.getApplication().apiClient.GetImageUrl(itemId, ImageOptions().also {
				it.imageType = type
				it.tag = tag
				if (index != null) it.imageIndex = index
			})
		}

		suspend fun getBitmap(context: Context) = withContext(Dispatchers.IO) {
			Log.i("Image URL", url)
			Picasso.with(context).load(url).get()
		}
	}
}