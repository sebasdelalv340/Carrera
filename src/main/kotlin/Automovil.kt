
/**
 * Clase que representa un automóvil, que es un tipo específico de vehículo.
 * @param nombre El nombre del automóvil.
 * @param marca La marca del automóvil.
 * @param modelo El modelo del automóvil.
 * @param capacidadCombustible La capacidad de combustible del automóvil en litros.
 * @param combustibleActual El nivel actual de combustible del automóvil en litros.
 * @param kilometrosActuales Los kilómetros totales recorridos por el automóvil.
 * @param esHibrido Indica si el automóvil es híbrido o no.
 */
class Automovil(nombre: String, marca: String, modelo: String, capacidadCombustible: Float, combustibleActual: Float, kilometrosActuales: Float, private val esHibrido: Boolean): Vehiculo(nombre, marca, modelo, capacidadCombustible, combustibleActual, kilometrosActuales) {

    override var paradas: Int = 0

    companion object{
        // Constantes para el cálculo de la autonomía y el derrape.
        const val KM_LITRO_HIBRIDO = 5.0f
        const val DERRAPE_GAS = 7.5f
        const val DERRAPE_HIBRIDO = 6.25f

        private var condicionBritanica: Boolean = false

        /**
         * Cambia la condición británica que afecta el comportamiento del vehículo.
         * @param nuevaCondicion La nueva condición británica.
         */
        fun cambiarCondicionBritanica(nuevaCondicion: Boolean) {
            condicionBritanica = nuevaCondicion
        }
    }

    /**
     * Calcula la autonomía del automóvil en función de si es híbrido o no.
     * @return La autonomía del automóvil en kilómetros.
     */
    override fun calcularAutonomia(): Float {
        return if (!esHibrido) {
            super.calcularAutonomia()
        } else {
            combustibleActual * KM_LITRO_HIBRIDO
        }
    }

    /**
     * Realiza un viaje con el automóvil, actualizando el nivel de combustible y los kilómetros recorridos.
     * @param distancia La distancia del viaje en kilómetros.
     * @return La distancia restante que no pudo ser recorrida debido a la falta de combustible.
     */
    override fun realizaViaje(distancia: Float): Float {
        if (distancia >= calcularAutonomia()) {
            val kmRecorre = calcularAutonomia()
            combustibleActual = 0.0f
            kilometrosActuales += calcularAutonomia()
            return distancia - kmRecorre
        } else {
            if (!esHibrido) {
                combustibleActual -= (distancia / KM_LITRO)
                kilometrosActuales += distancia
                return 0.0f
            } else {
                combustibleActual -= (distancia / KM_LITRO_HIBRIDO)
                kilometrosActuales += distancia
                return 0.0f
            }
        }
    }

    /**
     * Realiza un derrape con el automóvil, reduciendo el nivel de combustible.
     * @return El nivel de combustible restante después del derrape.
     */
    fun realizaDerrape(): Float {
        if (!esHibrido) {
            combustibleActual -= DERRAPE_GAS / calcularAutonomia()
            return combustibleActual
        } else {
            combustibleActual -= DERRAPE_HIBRIDO / calcularAutonomia()
            return combustibleActual
        }
    }
}