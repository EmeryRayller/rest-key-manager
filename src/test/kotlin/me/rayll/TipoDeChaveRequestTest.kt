package me.rayll

import me.rayll.keymanager.TipoDeChaveRequest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class TipoDeChaveRequestTest {

    @Nested
    inner class ChaveAleatoriaTest {

        @Test
        fun `deve ser valido quando a chave aleatoria for nula ou vazia`() {

            val tipoDeChave = TipoDeChaveRequest.ALEATORIA

            assertTrue(tipoDeChave.valida(null))
            assertTrue(tipoDeChave.valida(""))
        }

        @Test
        fun `nao deve ser valido quando a chave aleatoria possuir um valor`() {

            val tipoDeChave = TipoDeChaveRequest.ALEATORIA

            assertFalse(tipoDeChave.valida("umobugafeidital"))
        }
    }

    @Nested
    inner class ChaveCPFTest {

        @Test
        fun `deve ser valido quando o numero de cpf for valido`() {

            val tipoDeChave = TipoDeChaveRequest.CPF

            assertTrue(tipoDeChave.valida("52396796062"))
        }

        @Test
        fun `nao deve ser valido quando o numero de cpf for valido`() {

            val tipoDeChave = TipoDeChaveRequest.CPF

            assertFalse(tipoDeChave.valida("1122334415"))
        }

        @Test
        fun `nao deve ser valido quando o numero de cpf nao for informado`() {

            val tipoDeChave = TipoDeChaveRequest.CPF

            assertFalse(tipoDeChave.valida(""))
            assertFalse(tipoDeChave.valida(null))
        }

    }

    @Nested
    inner class CelularTest {

        @Test
        fun `deve ser valido quando celular for um numero valido`() {

            val tipoDechave = TipoDeChaveRequest.CELULAR

            assertTrue(tipoDechave.valida("+5511987654321"))
        }

        @Test
        fun `nao deve ser valido quando celular for um numero invalido`() {

            val tipoDechave = TipoDeChaveRequest.CELULAR

            assertFalse(tipoDechave.valida("11987654321"))
            assertFalse(tipoDechave.valida("+55a11987654321"))
        }

        @Test
        fun `nao deve ser valido quando celular for um numero nao for informado`() {

            val tipoDechave = TipoDeChaveRequest.CELULAR

            assertFalse(tipoDechave.valida(null))
            assertFalse(tipoDechave.valida(""))
        }
    }

    @Nested
    inner class EmailTest {

        @Test
        fun `deve ser valido quando email for endereco valido`() {

            val tipoDechave = TipoDeChaveRequest.EMAIL

            assertTrue(tipoDechave.valida("zup.edu@zup.com.br"))
        }

        @Test
        fun `nao deve ser valido quando email estiver em um formato invalido`() {

            val tipoDechave = TipoDeChaveRequest.EMAIL

            assertFalse(tipoDechave.valida("zup.eduzup.com.br"))
            assertFalse(tipoDechave.valida("zup.edu@zup.com."))
        }

        @Test
        fun `nao deve ser valido quando email nao for informado`() {

            val tipoDechave = TipoDeChaveRequest.EMAIL

            assertFalse(tipoDechave.valida(null))
            assertFalse(tipoDechave.valida(""))
        }
    }
}