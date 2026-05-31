/**
 * Ejercicio 5: It y Scope Functions (run, apply, also, let)
 *
 * Implementa los métodos de esta clase para que pasen todos los tests
 * del archivo Ejercicio5ItScopeFunctionsTest.kt
 *
 * IMPORTANTE: No modifiques la firma de los métodos, solo implementa su lógica.
 * IMPORTANTE: Debes usar las scope functions indicadas en cada sección.
 */

data class Usuario(
    var id: Int = 0,
    var nombre: String = "",
    var email: String = "",
    var activo: Boolean = false,
    var roles: MutableList<String> = mutableListOf(),
    var configuracion: ConfiguracionUsuario = ConfiguracionUsuario(),
)

data class ConfiguracionUsuario(
    var tema: String = "claro",
    var idioma: String = "es",
    var notificaciones: Boolean = true,
    var nivelPrivacidad: Int = 1,
)

data class Validacion(
    val campo: String,
    val valido: Boolean,
    val mensaje: String,
)

class UsuarioBuilder {
    // Parte A: Uso del parámetro implícito 'it'

    fun procesarNumeros(numeros: List<Int>): List<Int> {
        return numeros
            .filter { it % 2 == 0 }
            .map { it * 10 }
    }

    fun validarUsuarios(usuarios: List<Usuario>): List<List<Validacion>> {
        return usuarios.map {
            listOf(
                Validacion(
                    "nombre",
                    it.nombre.isNotBlank(),
                    if (it.nombre.isNotBlank()) "OK" else "Nombre vacío"
                ),
                Validacion(
                    "email",
                    it.email.contains("@"),
                    if (it.email.contains("@")) "OK" else "Email inválido"
                ),
                Validacion(
                    "roles",
                    it.roles.isNotEmpty(),
                    if (it.roles.isNotEmpty()) "OK" else "Sin roles"
                )
            )
        }
    }

    fun procesarTextos(textos: List<String>): List<String> {
        return textos
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
    }

    // Parte B: Función run

    fun calcularNivelAcceso(usuario: Usuario): Int {
        return usuario.run {
            var puntos = 0

            if (activo) puntos += 10
            puntos += roles.size * 5
            if (email.contains("@empresa.com")) puntos += 5

            puntos
        }
    }

    fun crearUsuarioConTipo(tipo: String): Usuario {
        return Usuario().apply {
            when (tipo) {
                "ADMIN" -> {
                    roles.add("ADMIN")
                    configuracion.nivelPrivacidad = 3
                    configuracion.notificaciones = true
                }

                "USER" -> {
                    roles.add("USER")
                    configuracion.nivelPrivacidad = 1
                    configuracion.notificaciones = false
                }
            }
        }
    }

    // Parte C: Función apply

    fun crearUsuarioCompleto(
        nombre: String,
        email: String,
        roles: List<String>,
    ): Usuario {
        return Usuario().apply {
            this.nombre = nombre
            this.email = email
            activo = true
            this.roles.addAll(roles)
            configuracion = ConfiguracionUsuario()
        }
    }

    fun actualizarUsuario(
        usuario: Usuario,
        actualizacion: Usuario.() -> Unit,
    ): Usuario {
        return usuario.apply(actualizacion)
    }

    // Parte D: Función also

    fun crearUsuarioConLog(
        nombre: String,
        email: String,
        onLog: (String) -> Unit,
    ): Usuario {
        return Usuario()
            .also {
                it.nombre = nombre
                onLog("Usuario creado: $nombre")
            }
            .also {
                it.email = email
                onLog("Email asignado: $email")
            }
            .also {
                it.activo = true
                onLog("Usuario activado")
            }
    }

    fun crearYValidar(
        nombre: String,
        email: String,
    ): Pair<Usuario, Boolean> {
        var valido = false

        val usuario = Usuario().also {
            it.nombre = nombre
            it.email = email
            valido = nombre.isNotBlank() && email.contains("@")
        }

        return usuario to valido
    }

    // Parte E: Función let

    fun procesarEmailOpcional(email: String?): String {
        return email?.let {
            "Usuario con email: $it"
        } ?: "Usuario sin email"
    }

    fun generarMensajesBienvenida(usuarios: List<Usuario>): List<String> {
        return usuarios.mapNotNull { usuario ->
            usuario.email.takeIf {
                usuario.activo && it.isNotBlank()
            }?.let {
                "Bienvenido/a ${usuario.nombre} ($it)"
            }
        }
    }

    // Parte F: Combinación de Scope Functions

    fun procesarUsuarioComplejo(datosBase: Map<String, String>): Usuario? {
        val nombre = datosBase["nombre"] ?: return null
        val email = datosBase["email"] ?: return null

        return Usuario().run {
            apply {
                this.nombre = nombre
                this.email = email
                activo = true
            }.also {
                if (datosBase["departamento"] == "IT") {
                    it.configuracion.tema = "oscuro"
                    it.roles.add("IT_USER")
                }
            }
        }
    }

    fun procesarLoteUsuarios(usuarios: List<Usuario>): List<Usuario> {
        return usuarios.map { usuario ->
            usuario
                .apply {
                    activo = true
                    configuracion.notificaciones = true
                }
                .also {
                    if (it.roles.isEmpty()) {
                        it.roles.add("USER")
                    }
                }
                .run {
                    if (nombre == "Admin") {
                        roles.add("ADMIN")
                        configuracion.nivelPrivacidad = 3
                    }
                    this
                }
        }
    }

    fun parsearYCrearUsuario(datosRaw: String): Usuario? {
        return run {
            try {
                val datos =
                    datosRaw
                        .replace("\n", "")
                        .split("|")
                        .map { it.trim() }
                        .filter { it.contains(":") }
                        .associate {
                            val partes = it.split(":", limit = 2)
                            partes[0] to partes[1]
                        }

                Usuario().apply {
                    id = datos["id"]?.toIntOrNull() ?: 0
                    nombre = datos["nombre"] ?: ""
                    email = datos["email"] ?: ""
                    activo = datos["activo"]?.toBoolean() ?: false

                    roles =
                        datos["roles"]
                            ?.split(",")
                            ?.map { it.trim() }
                            ?.toMutableList()
                            ?: mutableListOf()

                    configuracion = ConfiguracionUsuario().apply {
                        tema = datos["tema"] ?: "claro"
                        idioma = datos["idioma"] ?: "es"
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
