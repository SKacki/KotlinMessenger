package com.inzynierka.komunikat.utils

import com.inzynierka.komunikat.classes.User

/**
 * Interfejs filtrów do filtrowania listy użytkowników.
 */
interface GetUsersFilter {
    /**
     * Metoda filtrująca listę użytkowników.
     *
     * @param userList lista użytkowników do filtrowania
     * @return filtrowana lista użytkowników
     */
    fun filter(userList: List<User>): List<User>
}

/**
 * Klasa filtrująca listę użytkowników po imieniu.
 * Filtruje tylko te użytkowników, których imię zaczyna się od podanego ciągu znaków.
 *
 * @param searchUserName ciąg znaków, od którego mają zaczynać się imiona użytkowników przechodzących filtr
 */
class FilterUsersByNameStartingWith(val searchUserName: String) : GetUsersFilter {
    override fun filter(userList: List<User>): List<User> {
        return userList.filter { ff ->
            ff.name.startsWith(searchUserName)
        }
    }
}

/**
 * Klasa filtrująca listę użytkowników, usuwająca z niej określonego użytkownika.
 *
 * @param currentUser użytkownik, który ma zostać usunięty z listy
 */
class FilterOutUser(val currentUser: User) : GetUsersFilter {
    override fun filter(userList: List<User>): List<User> {
        return userList.filterNot { ff ->
            currentUser.uid == ff.uid
        }
    }
}
