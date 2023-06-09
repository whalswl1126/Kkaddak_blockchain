package com.ssafy.kkaddak.data.remote.repository

import com.ssafy.kkaddak.common.util.wrapToResource
import com.ssafy.kkaddak.data.remote.Resource
import com.ssafy.kkaddak.data.remote.datasource.profile.NFTImageResponse
import com.ssafy.kkaddak.data.remote.datasource.profile.ProfileRemoteDataSource
import com.ssafy.kkaddak.domain.entity.profile.FollowerItem
import com.ssafy.kkaddak.domain.entity.profile.ProfileItem
import com.ssafy.kkaddak.domain.entity.song.SongItem
import com.ssafy.kkaddak.domain.repository.ProfileRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource
) : ProfileRepository {

    override suspend fun getProfileInfo(nickname: String): Resource<ProfileItem> =
        wrapToResource {
            profileRemoteDataSource.getProfileInfo(nickname).toDomainModel()
        }

    override suspend fun getProfileSong(nickname: String): Resource<List<SongItem>> =
        wrapToResource {
            profileRemoteDataSource.getProfileSong(nickname).map { it.toDomainModel() }
        }

    override suspend fun deleteMySong(songId: String) {
        profileRemoteDataSource.deleteMySong(songId)
    }

    override suspend fun followArtist(artistId: String) {
        profileRemoteDataSource.followArtist(artistId)
    }

    override suspend fun unfollowArtist(artistId: String) {
        profileRemoteDataSource.unfollowArtist(artistId)
    }

    override suspend fun getFollowers(lastId: Int, limit: Int): Resource<List<FollowerItem>> =
        wrapToResource {
            profileRemoteDataSource.getFollowers(lastId, limit).map { it.toDomainModel() }
        }

    override suspend fun getFollowings(lastId: Int, limit: Int): Resource<List<FollowerItem>> =
        wrapToResource {
            profileRemoteDataSource.getFollowings(lastId, limit).map { it.toDomainModel() }
        }

    override suspend fun uploadThumbnail(nftImageUrl: String) {
        profileRemoteDataSource.uploadThumbnail(nftImageUrl)
    }

    override suspend fun editUserInfo(
        isUpdating: Boolean,
        nickname: String,
        profileImg: MultipartBody.Part?
    ) {
        profileRemoteDataSource.editUserInfo(isUpdating, nickname, profileImg)
    }

    override suspend fun uploadNFTImage(
        songId: String,
        nftImg: MultipartBody.Part?
    ): Resource<NFTImageResponse> =
        wrapToResource {
            profileRemoteDataSource.uploadNFTImage(songId, nftImg)
        }

    override suspend fun changeSongState(songStatus: String, songUUID: String): Resource<Boolean> =
        wrapToResource {
            profileRemoteDataSource.changeSongState(songStatus, songUUID)
        }
}