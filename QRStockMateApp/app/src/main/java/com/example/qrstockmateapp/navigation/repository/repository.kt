package com.example.qrstockmateapp.navigation.repository

import com.example.qrstockmateapp.api.models.Company
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.models.Message
import com.example.qrstockmateapp.api.models.TransportRoute
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Vehicle
import com.example.qrstockmateapp.api.models.Warehouse

object DataRepository {
    private var user: User? = null
    private var item: Item? = null
    private var company: Company? =null
    private var warehouses: List<Warehouse>? = null
    private var employees: List<User>? = null
    private var vehicles: List<Vehicle>? = null
    private var token: String = ""

    //Pasar elemento de una pantalla a otra
    private var warehousePlus: Warehouse? = null
    private var userPlus: User? = null
    private var routePlus: TransportRoute? = null
    private var vehiclePlus: Vehicle? = null
    private var routeSplash: String? = null
    private var newMessages: Int? = null
    private var messages: List<Message>? = null
    private var currentScreenIndex: Int = 0
    private var listNewMessage: List<Int>? = null
    private var onHideTextField : Boolean = false



    fun getListNewMessage():List<Int>?{
        return listNewMessage
    }

    fun setListNewMessage(list:List<Int>){
        listNewMessage = list
    }

    fun getOnHide():Boolean{
        return onHideTextField
    }
    fun setOnHide(hide:Boolean){
        onHideTextField = hide
    }


    fun getCurrentScreenIndex():Int{
        return currentScreenIndex
    }
    fun setCurrentScreenIndex(index:Int){
        currentScreenIndex = index
    }


    fun setNewMessages(number :Int){
        newMessages = number
    }

    fun getNewMessages(): Int?{
        return newMessages
    }


    fun setMessages(newMessages: List<Message>) {
        messages = newMessages
    }

    fun getMessages():List<Message>? {
        return messages
    }


    fun setVehiclePlus(vehicle: Vehicle) {
        vehiclePlus = vehicle
    }

    fun getVehiclePlus():Vehicle? {
        return vehiclePlus
    }


    fun setSplash(route: String) {
        routeSplash = route
    }

    fun getSplash():String? {
        return routeSplash
    }



    fun setVehicles(newVehicles: List<Vehicle>) {
        vehicles = newVehicles
    }

    fun getVehicles(): List<Vehicle>?{
        return vehicles
    }

    fun setRoutePlus(newRoute: TransportRoute){
        routePlus = newRoute
    }

    fun getRoutePlus():TransportRoute?{
        return routePlus
    }

    fun setUserPlus(newUser: User){
        userPlus = newUser
    }

    fun getUserPlus():User?{
        return userPlus
    }
    fun setWarehousePlus(newWarehouse: Warehouse){
        warehousePlus = newWarehouse
    }

    fun getWarehousePlus():Warehouse?{
        return warehousePlus
    }




    fun setEmployees(newEmployees: List<User>) {
        employees= newEmployees
    }

    fun getEmployees(): List<User>?{
        return employees
    }
    fun setWarehouses(newWarehouses: List<Warehouse>) {
        warehouses= newWarehouses
    }

    fun getWarehouses(): List<Warehouse>?{
        return warehouses
    }

    fun setCompany(newCompany: Company) {
        company = newCompany
    }

    fun getCompany(): Company?{
        return company
    }

    fun setToken(newToken: String) {
        token = newToken
    }

    fun getToken(): String {
        return token
    }

    fun setUser(newUser: User) {
        user = newUser
    }

    fun getUser(): User? {
        return user
    }

    fun setItem(newItem: Item) {
        item = newItem
    }

    fun getItem(): Item? {
        return item
    }

    fun LogOut(){
        user = null
        company = null
        token = ""
        warehouses = null
        employees = null
        currentScreenIndex = 0

    }

}
