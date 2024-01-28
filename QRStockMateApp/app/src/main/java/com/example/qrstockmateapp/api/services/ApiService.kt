package com.example.qrstockmateapp.api.services

import com.example.qrstockmateapp.api.models.Company
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.models.Message
import com.example.qrstockmateapp.api.models.Transaction
import com.example.qrstockmateapp.api.models.TransportRoute
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Vehicle
import com.example.qrstockmateapp.api.models.Warehouse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("User/SignIn/")
    suspend fun signIn(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("User/Company/")
    suspend fun getCompanyByUser(
        @Body user: User
    ): Response<Company>

    @POST("Company/Warehouse/")
    suspend fun getWarehouse(
        @Body company: Company
    ): Response<List<Warehouse>>

    @POST("Company/Employees/")
    suspend fun getEmployees(
        @Body company: Company
    ): Response<List<User>>

    @POST("User/SignUp")
    suspend fun signUp(
        @Body registrationBody: RegistrationBody
    ): Response<voidResponse>

    @POST("Warehouse/{id}")
    suspend fun createWarehouse(
        @Path("id") id: Int,
        @Body warehouse: Warehouse
    ): Response<voidResponse>
    @PUT("Warehouse/")
    suspend fun updateWarehouse(
        @Body warehouse: Warehouse
    ): Response<voidResponse>
    @PUT("User/")
    suspend fun updateUser(
        @Body user: User
    ): Response<voidResponse>

    @HTTP(method = "DELETE", path = "User/DeleteAccount", hasBody = true) //Para un Delete con Body hay que hacerlo asi
    suspend fun deleteAccount(
        @Body user: User
    ): Response<Unit>

    @GET("Message/NewMessage/{format}")
    suspend fun getNewMessages(
        @Path("format") format: String,
    ): Response<Int>

    @HTTP(method = "DELETE", path = "Message/DeleteConversation", hasBody = true) //Para un Delete con Body hay que hacerlo asi
    suspend fun deleteConversation(
        @Body users: String
    ): Response<Void>

    @GET("Warehouse/GetItems/{id}")
    suspend fun getItems(
        @Path("id") id: Int,
    ): Response<List<Item>>

    @Multipart
    @POST("Warehouse/UpdateImage")
    suspend fun updateImageWarehouse(
        @Part("warehouseId") warehouseId: RequestBody,
        @Part image: MultipartBody.Part
    ):Response<Void>


    @Multipart
    @POST("Message/UploadAudio/")
    suspend fun uploadAudio(
        @Part audio: MultipartBody.Part,
        @Part("code") code: RequestBody,
        @Part("senderContactId") senderContactId: RequestBody,
        @Part("receiverContactId") receiverContactId: RequestBody,
        @Part("content") content: RequestBody,
        @Part("sentDate") sentDate: RequestBody,
        @Part("type") type: RequestBody
    ): Response<Void>


    @Multipart
    @POST("User/UpdateImage")
    suspend fun updateImageUser(
        @Part("userId") userId: RequestBody,
        @Part image: MultipartBody.Part
    ):Response<Void>
    @Multipart
    @POST("Item/UpdateImage")
    suspend fun updateImageItem(
        @Part("itemId") itemId: RequestBody,
        @Part image: MultipartBody.Part
    ):Response<Void>
    @HTTP(method = "DELETE", path = "Warehouse/{idCompany}", hasBody = true) //Para un Delete con Body hay que hacerlo asi
    suspend fun deleteWarehouse(
        @Path("idCompany") id: Int,
        @Body warehouse: Warehouse
    ): Response<Void>

    @GET("Item/Search/{name}")
    suspend fun searchItem(
        @Path("name") id: String,
    ): Response<List<Item>>

    @PUT("Item/")
    suspend fun updateItem(
        @Body item: Item
    ): Response<voidResponse>

    @POST("TransactionHistory/")
    suspend fun addHistory(
        @Body transaction:Transaction
    ): Response<voidResponse>

    @GET("TransactionHistory/History/{code}")
    suspend fun getHistory(
        @Path("code") code: String
    ): Response<List<Transaction>>

    @POST("Warehouse/AddItem/{Id}")
    suspend fun addItem(
        @Path("Id") Id: Int,
        @Body item: Item
    ):Response<Void>


    @GET("Message/MessageByCode/{code}")
    suspend fun getMessageByCode(
        @Path("code") code: String
    ): Response<List<Message>>

    @POST("Message/")
    suspend fun sendMessage(
        @Body message: Message
    ):Response<Void>





    @GET("TransportRoute/TransportRoutes/{code}")
    suspend fun getTransportRoutes(
        @Path("code") code: String
    ): Response<List<TransportRoute>>

    @GET("TransportRoute/TransportRouteById/{id}")
    suspend fun getTransportRoute(
        @Path("id") id: Int
    ): Response<TransportRoute>

    @PUT("TransportRoute/")
    suspend fun putTransportRoutes(
        @Body transportRoute: TransportRoute
    ): Response<Void>

    @HTTP(method = "DELETE", path = "TransportRoute/", hasBody = true)
    suspend fun deleteTransportRoutes(
        @Body transportRoute: TransportRoute
    ): Response<Void>
    @POST("TransportRoute/")
    suspend fun addTransportRoutes(
        @Body transportRoute: TransportRoute
    ): Response<TransportRoute>


    @PUT("TransportRoute/InitRoute/{id}")
    suspend fun initRoute(
        @Path("id") id: Int
    ): Response<String>

    @PUT("TransportRoute/FinishRoute/{id}")
    suspend fun finishRoute(
        @Path("id") id: Int
    ): Response<String>



    @GET("Company/Vehicles/{code}")
    suspend fun getVehicles(
        @Path("code") code: String
    ): Response<List<Vehicle>>

    @HTTP(method = "DELETE", path = "Vehicle/", hasBody = true)
    suspend fun deleteVehicle(
        @Body vehicle: Vehicle
    ): Response<Void>

    @PUT("Vehicle/")
    suspend fun updateVehicle(
        @Body vehicle: Vehicle
    ):Response<Vehicle>

    @POST("Vehicle/")
    suspend fun addVehicle(
        @Body vehicle: Vehicle
    ):Response<Vehicle>

    @GET("Vehicle/GetLocation/{Id}")
    suspend fun getLocationVehicle(
        @Path("Id") Id: Int,
    ):Response<Vehicle>


    @PUT("Vehicle/UpdateLocation/{Id}")
    suspend fun updateLocationVehicle(
        @Path("Id") Id: Int,
        @Body location: String
    ):Response<Void>


}


data class LoginResponse(
    val user: User,
    val token: String
)
data class RegistrationBody(
    val user:User,
    val company: Company
)

data class voidResponse(
    val success: Boolean,
    val message: String? // Mensaje descriptivo opcional
)
