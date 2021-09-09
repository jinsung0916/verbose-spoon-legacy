package com.benope.verbose.spoon.web.user.service

import com.benope.verbose.spoon.core_backend.security.domain.User
import com.benope.verbose.spoon.core_backend.security.repository.UserRepository
import com.benope.verbose.spoon.web.user.dto.CreateUserRequest
import com.benope.verbose.spoon.web.user.dto.UpdateUserPasswordRequest
import com.benope.verbose.spoon.web.user.dto.UpdateUserRequest
import com.benope.verbose.spoon.web.user.exception.DuplicatedUserException
import com.benope.verbose.spoon.web.user.exception.UserNotExistsException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserManageService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun createUser(createUserRequest: CreateUserRequest?): User {
        if (isDuplicated(createUserRequest?.username)) {
            throw DuplicatedUserException()
        }

        return createUserRequest?.toUserEntity(passwordEncoder)
            ?.let { userRepository.save(it) }
            ?: throw IllegalArgumentException()
    }

    private fun isDuplicated(username: String?): Boolean {
        return userRepository.findByUsername(username) != null
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #username == authentication.principal.username")
    fun findUser(username: String?): User {
        return userRepository.findByUsername(username) ?: throw UserNotExistsException()
    }

    fun findUserList(pageable: Pageable?): Page<User> {
        return pageable?.let { userRepository.findAll(it) } ?: Page.empty()
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #username == authentication.principal.username")
    fun updateUser(username: String?, updateUserRequest: UpdateUserRequest?): User {
        val user = findUser(username)
        updateUserRequest?.updateUserEntity(user)
        return userRepository.save(user)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #username == authentication.principal.username")
    fun deleteUser(username: String?) {
        val user = findUser(username)
        return userRepository.deleteById(user.userId!!)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or #username == authentication.principal.username")
    fun updateUserPassword(username: String?, updateUserPasswordRequest: UpdateUserPasswordRequest?): User? {
        val user = findUser(username)
        updateUserPasswordRequest?.updateUserEntity(user, passwordEncoder)
        return userRepository.save(user)
    }

}