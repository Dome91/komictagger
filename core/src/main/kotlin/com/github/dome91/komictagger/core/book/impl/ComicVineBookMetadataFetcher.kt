package com.github.dome91.komictagger.core.book.impl

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.dome91.komictagger.core.book.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.jackson.responseObject
import dev.brachtendorf.jimagehash.hash.Hash
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash
import javax.imageio.ImageIO


class ComicVineBookMetadataFetcher : BookMetadataFetcher {

	override fun fetch(request: BookMetadataFetchRequest, bookReaderWriter: BookReaderWriter): BookMetadata? {
		val cover = bookReaderWriter.readCover() ?: return null
		val hasher = PerceptiveHash(32)
		val coverHash = hasher.hash(cover)
		val volumeDetailUrl = getVolumeDetailUrl(request, coverHash, hasher) ?: return null

		return null
	}

	private tailrec fun getVolumeDetailUrl(
		request: BookMetadataFetchRequest,
		coverHash: Hash,
		hasher: HashingAlgorithm,
		offset: Int = 0,
		total: Int = Int.MAX_VALUE,
		detailUrl: String? = null
	): String? {
		if (detailUrl != null || offset > total) {
			return detailUrl
		}

		val parameters = listOf(
			Pair("api_key", "3c07edca6419b23c09dd5a8587ffc2278b9d5e07"),
			Pair("format", "json"),
			Pair("filter", "name:${request.searchTerm}"),
			Pair("offset", offset)
		)

		val response = Fuel.get(getComicVineUrl(request.format), parameters)
			.appendHeader("User-Agent", "KomicTagger")
			.responseObject<ComicVineResult>()
			.third.get()

		val hit = response.results.firstOrNull { result ->
			val bytes = Fuel.get(result.image.mediumUrl).response().third.get()
			val image = ImageIO.read(bytes.inputStream())

			val hash = hasher.hash(image)
			val similarityScore = coverHash.normalizedHammingDistance(hash)
			similarityScore < .1
		}

		return getVolumeDetailUrl(request, coverHash, hasher, offset + 100, response.numberOfTotalsResults, hit?.apiDetailUrl)
	}

	private fun getIssueDetailUrl(volumeDetailUrl: String): String? {
		val parameters = listOf(
			Pair("api_key", "3c07edca6419b23c09dd5a8587ffc2278b9d5e07"),
			Pair("format", "json"),
			Pair("filter", "name:${request.searchTerm}"),
			Pair("offset", offset)
		)

		val response = Fuel.get(getComicVineUrl(request.format), parameters)
			.appendHeader("User-Agent", "KomicTagger")
			.responseObject<ComicVineResult>()
			.third.get()

		val hit = response.results.firstOrNull { result ->
			val bytes = Fuel.get(result.image.mediumUrl).response().third.get()
			val image = ImageIO.read(bytes.inputStream())

			val hash = hasher.hash(image)
			val similarityScore = coverHash.normalizedHammingDistance(hash)
			similarityScore < .1
		}

		return getVolumeDetailUrl(request, coverHash, hasher, offset + 100, response.numberOfTotalsResults, hit?.apiDetailUrl)
	}


	private fun getComicVineUrl(bookFormat: BookFormat?) = when (bookFormat) {
		BookFormat.VOLUME -> "https://comicvine.gamespot.com/api/volumes"
		BookFormat.ISSUE -> "https://comicvine.gamespot.com/api/issues"
		null -> "https://comicvine.gamespot.com/api/volumes"
	}
}

private data class ComicVineResult(
	val error: String,
	val limit: Int,
	val offset: Int,
	@JsonProperty("number_of_page_results") val numberOfPageResults: Int,
	@JsonProperty("number_of_total_results") val numberOfTotalsResults: Int,
	@JsonProperty("status_code") val statusCode: Int,
	val results: List<Result>
) {
	companion object {
		data class Result(
			@JsonProperty("api_detail_url") val apiDetailUrl: String?,
			val image: Image,
		)
	}
}

data class Image(
	@JsonProperty("icon_url") val iconUrl: String,
	@JsonProperty("medium_url") val mediumUrl: String,
	@JsonProperty("screen_url") val screenUrl: String,
	@JsonProperty("screen_large_url") val screenLargeUrl: String,
	@JsonProperty("small_url") val smallUrl: String,
	@JsonProperty("super_url") val superUrl: String,
	@JsonProperty("thumb_url") val thumbUrl: String,
	@JsonProperty("tiny_url") val tinyUrl: String,
	@JsonProperty("original_url") val originalUrl: String,
	@JsonProperty("image_tags") val imageTags: String?
)
