package ir.ghararemaghzha.game.data

import retrofit2.http.Field

class ApiRepository(
        private val api: NetworkApi

) : BaseRepository() {
    suspend fun validate(
            Token: String,
            number: String
    ) = safeApiCall { api.validate(Token, number) }

    suspend fun appOpen(
            Token: String,
            number: String
    ) = safeApiCall { api.appOpen(Token, number) }

    suspend fun registerUser(
            name: String,
            number: String
    ) = safeApiCall { api.registerUser(name, number) }


    suspend fun verification(
            number: String,
            code: String,
            fbToken: String
    ) = safeApiCall { api.verification(number, code, fbToken) }


    suspend fun login(
            @Field("number") number: String
    ) = safeApiCall { api.login(number) }


    suspend fun resend(
            number: String
    ) = safeApiCall { api.resend(number) }


    suspend fun getServerTime(
            Token: String,
            number: String
    ) = safeApiCall { api.getServerTime(Token, number) }


    suspend fun sendMessage(
            Token: String,
            number: String,
            message: String
    ) = safeApiCall { api.sendMessage(Token, number, message) }


    suspend fun getMessages(
            Token: String,
            number: String,
            lastUpdate: String
    ) = safeApiCall { api.getMessages(Token, number, lastUpdate) }


    suspend fun getQuestions(
            Token: String,
            number: String,
            start: Int,
            size: Int
    ) = safeApiCall { api.getQuestions(Token, number, start, size) }


    suspend fun getHighscoreList(
            Token: String,
            number: String
    ) = safeApiCall { api.getHighscoreList(Token, number) }


    suspend fun getBuyHistory(
            Token: String,
            number: String
    ) = safeApiCall { api.getBuyHistory(Token, number) }


    suspend fun initBuy(
            Token: String,
            number: String,
            plan: String,
            influencerId: String,
            influencerAmount: String,
            amount: String
    ) = safeApiCall { api.initBuy(Token, number, plan, influencerId, influencerAmount, amount) }


    suspend fun searchInfluencer(
            Token: String,
            number: String,
            code: String
    ) = safeApiCall { api.searchInfluencer(Token, number, code) }


    suspend fun answerQuestion(
            Token: String,
            number: String,
            questionId: String,
            userAnswer: String,
            booster: String,
            season: Int
    ) = safeApiCall { api.answerQuestion(Token, number, questionId, userAnswer,booster,season) }


    suspend fun sendScore(
            Token: String,
            number: String,
            score: String,
            season: Int
    ) = safeApiCall { api.sendScore(Token, number, score,season) }


    suspend fun sendQuestionCount(
            Token: String,
            number: String,
            count: String
    ) = safeApiCall { api.sendQuestionCount(Token, number, count) }


    suspend fun alterAvatar(
            Token: String,
            number: String,
            option: String,
            image: String,
            avatar: String
    ) = safeApiCall { api.alterAvatar(Token, number, option, image, avatar) }


    suspend fun updateProfile(
            Token: String,
            number: String,
            userName: String,
            userEmail: String,
            userBday: String,
            userSex: String,
            invite: String
    ) = safeApiCall { api.updateProfile(Token, number, userName, userEmail, userBday, userSex, invite) }


    suspend fun getPlans(
            Token: String,
            number: String
    ) = safeApiCall { api.getPlans(Token, number) }

    suspend fun getNitro(
            Token: String,
            number: String
    ) = safeApiCall { api.getNitro(Token, number) }


    suspend fun getInvites(
            Token: String,
            number: String
    ) = safeApiCall { api.getInvites(Token, number) }


    suspend fun getSlider() = safeApiCall { api.getSlider() }


    suspend fun getUserDetails(
            Token: String,
            number: String,
            userId: String
    ) = safeApiCall { api.getUserDetails(Token, number, userId) }

    suspend fun report(
            Token: String,
            number: String,
            questionId: String
    ) = safeApiCall { api.report(Token, number,questionId) }

    suspend fun info(
            Token: String,
            number: String,
            info: String
    ) = safeApiCall { api.info(Token, number,info) }


    suspend fun scoreBooster(
            Token: String,
            number: String,
            scoreBooster: String
    ) = safeApiCall { api.scoreBooster(Token, number,scoreBooster) }
}