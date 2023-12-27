package com.example.qrstockmateapp.navigation.repository

import com.example.qrstockmateapp.api.models.Company
import com.example.qrstockmateapp.api.models.Item
import com.example.qrstockmateapp.api.models.User
import com.example.qrstockmateapp.api.models.Warehouse

object DataRepository {
    private var user: User? = null
    private var item: Item? = null
    private var company: Company? =null
    private var warehouses: List<Warehouse>? = null
    private var employees: List<User>? = null
    private var token: String = ""
    //Pasar warehouse de una pantalla a otra
    private var warehousePlus: Warehouse? = null
    private var userPlus: User? = null


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
        item = newItem;
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
    }

}
