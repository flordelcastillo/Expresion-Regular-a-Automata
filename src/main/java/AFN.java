import java.util.*;
import java.util.stream.Collectors;

// Clase que representa un Autómata Finito No Determinista (AFN)
public class AFN {

    // Estado inicial del AFN
    public Estado estadoInicial;

    // Conjunto de estados finales
    public Set<Estado> estadosFinales = new HashSet<>();

    // Lista de transiciones del autómata
    public List<Transicion> transiciones = new ArrayList<>();

    // Conjunto de todos los estados del autómata
    public Set<Estado> estados = new HashSet<>();

    // Alfabeto (símbolos válidos)
    public Set<String> alfabeto = new HashSet<>();

    // Agrega un estado al conjunto de estados
    public void agregarEstado(Estado estado) {
        estados.add(estado);
    }

    // Agrega una transición al autómata
    public void agregarTransicion(Estado desde, String simbolo, Estado hacia) {
        transiciones.add(new Transicion(desde, simbolo, hacia));
        estados.add(desde);
        estados.add(hacia);
        alfabeto.add(simbolo);
    }

    // Marca un estado como final
    public void agregarEstadoFinal(Estado estado) {
        estadosFinales.add(estado);
    }

    // Establece el estado inicial del autómata
    public void setEstadoInicial(Estado estado) {
        estadoInicial = estado;
        estados.add(estado);
    }

    // Concatena este AFN con otro
    public void concatenarAFN(AFN otro) {
        // Paso 1: Renombrar los estados del segundo AFN para evitar colisiones de ID
        int offset = this.obtenerMaxIdEstado() + 1;
        otro.renombrarEstadosConOffset(offset);

        // Paso 2: Crear transiciones lambda desde cada estado final actual al estado inicial del otro AFN
        for (Estado final1 : this.estadosFinales) {
            this.agregarTransicion(final1, "_", otro.estadoInicial);
        }

        // Paso 3: Unir todos los componentes del segundo AFN a este
        this.estados.addAll(otro.estados);
        this.transiciones.addAll(otro.transiciones);
        this.alfabeto.addAll(otro.alfabeto);

        // Paso 4: Actualizar el conjunto de estados finales (se reemplazan por los del otro AFN)
        this.estadosFinales = new HashSet<>(otro.estadosFinales);
    }

    // Renombra los estados sumando un offset a sus IDs (para evitar colisiones al unir AFNs)
    public void renombrarEstadosConOffset(int offset) {
        Map<Estado, Estado> nuevoEstadoMap = new HashMap<>();
        Set<Estado> nuevosEstados = new HashSet<>();

        for (Estado est : estados) {
            Estado nuevo = new Estado(est.id + offset);
            nuevo.nombre = "q" + (est.id + offset);
            nuevoEstadoMap.put(est, nuevo);
            nuevosEstados.add(nuevo);
        }

        estados = nuevosEstados;
        estadoInicial = nuevoEstadoMap.get(estadoInicial);

        Set<Estado> nuevosFinales = new HashSet<>();
        for (Estado ef : estadosFinales) {
            nuevosFinales.add(nuevoEstadoMap.get(ef));
        }
        estadosFinales = nuevosFinales;

        List<Transicion> nuevasTransiciones = new ArrayList<>();
        for (Transicion t : transiciones) {
            Estado nuevoDesde = nuevoEstadoMap.get(t.desde);
            Estado nuevoHacia = nuevoEstadoMap.get(t.hacia);
            nuevasTransiciones.add(new Transicion(nuevoDesde, t.simbolo, nuevoHacia));
        }
        transiciones = nuevasTransiciones;
    }

    // Devuelve el mayor ID entre todos los estados
    public int obtenerMaxIdEstado() {
        int max = -1;
        for (Estado e : estados) {
            if (e.id > max) {
                max = e.id;
            }
        }
        return max;
    }

    // Calcula el cierre lambda (ε-cierre) de un conjunto de estados
    public Set<Estado> cierreLambda(Set<Estado> estados) {
        Set<Estado> cierre = new HashSet<>(estados);
        Stack<Estado> pila = new Stack<>();
        pila.addAll(estados);

        while (!pila.isEmpty()) {
            Estado e = pila.pop();
            for (Transicion t : transiciones) {
                // Si hay una transición λ desde el estado actual a otro que aún no está en el cierre
                if (t.desde.equals(e) && t.simbolo.equals("_") && !cierre.contains(t.hacia)) {
                    cierre.add(t.hacia);
                    pila.push(t.hacia);
                }
            }
        }

        return cierre;
    }

    // Mueve desde un conjunto de estados mediante un símbolo específico
    public Set<Estado> mover(Set<Estado> estados, String simbolo) {
        Set<Estado> resultado = new HashSet<>();
        for (Estado e : estados) {
            for (Transicion t : transiciones) {
                if (t.desde.equals(e) && t.simbolo.equals(simbolo)) {
                    resultado.add(t.hacia);
                }
            }
        }
        return resultado;
    }

    // Convierte el AFN a un AFD usando el algoritmo de subconjuntos
    public AFD convertirADeterminista() {
        AFD afd = new AFD();
        Map<String, Estado> subconjuntoToEstado = new HashMap<>();
        Queue<Set<Integer>> pendientes = new LinkedList<>();

        // Paso 1: Calcular el cierre lambda del estado inicial
        Set<Estado> cierreInicial = cierreLambda(Set.of(estadoInicial));
        Set<Integer> conjuntoInicial = cierreInicial.stream().map(e -> e.id).collect(Collectors.toSet());

        // Crear estado inicial del AFD
        String claveInicial = clave(conjuntoInicial);
        Estado estadoInicialAFD = new Estado(conjuntoInicial);
        afd.estadoInicial = estadoInicialAFD;
        afd.estados.add(estadoInicialAFD);
        subconjuntoToEstado.put(claveInicial, estadoInicialAFD);
        pendientes.add(conjuntoInicial);

        // Paso 2: Proceso de construcción del AFD
        while (!pendientes.isEmpty()) {
            Set<Integer> actual = pendientes.poll(); // poll() obtiene y elimina el primer elemento de la cola
            String claveActual = clave(actual);
            Estado estadoDesde = subconjuntoToEstado.get(claveActual);

            for (String simbolo : alfabeto) {
                if (simbolo.equals("_")) continue; // Ignorar transiciones lambda

                // Mover y calcular cierre
                Set<Estado> estadosActuales = obtenerEstadosPorIDs(actual);
                Set<Estado> alcanzados = mover(estadosActuales, simbolo);
                Set<Estado> cierreAlcanzados = cierreLambda(alcanzados);
                Set<Integer> conjuntoAlcanzado = cierreAlcanzados.stream().map(e -> e.id).collect(Collectors.toSet());

                if (conjuntoAlcanzado.isEmpty()) continue;

                String claveAlcanzado = clave(conjuntoAlcanzado);
                Estado estadoHacia = subconjuntoToEstado.get(claveAlcanzado);

                // Si aún no se creó el estado, lo agregamos
                if (estadoHacia == null) {
                    estadoHacia = new Estado(conjuntoAlcanzado);
                    subconjuntoToEstado.put(claveAlcanzado, estadoHacia);
                    afd.estados.add(estadoHacia);
                    pendientes.add(conjuntoAlcanzado);
                }

                // Crear transición en el AFD
                afd.transiciones.add(new Transicion(estadoDesde, simbolo, estadoHacia));
            }
        }

        // Paso 3: Determinar qué estados del AFD son finales
        Set<Integer> idsFinales = estadosFinales.stream().map(e -> e.id).collect(Collectors.toSet());
        for (Estado estadoAFD : afd.estados) {
            for (Integer id : estadoAFD.conjunto) {
                if (idsFinales.contains(id)) {
                    afd.estadosFinales.add(estadoAFD);
                    break;
                }
            }
        }

        // Paso 4: Copiar el alfabeto sin el símbolo de lambda
        afd.alfabeto.addAll(alfabeto);
        afd.alfabeto.remove("_");

        return afd;
    }

    // Crea una clave única para representar un conjunto de IDs de estados
    private String clave(Set<Integer> conjunto) {
        return conjunto.stream().sorted().map(String::valueOf).collect(Collectors.joining(","));
    }

    // Devuelve el conjunto de estados correspondiente a un conjunto de IDs
    private Set<Estado> obtenerEstadosPorIDs(Set<Integer> ids) {
        return estados.stream().filter(e -> ids.contains(e.id)).collect(Collectors.toSet());
    }

    // Aplica la clausura de Kleene sobre este AFN
    public void aplicarClausuraKleene() {
        int offset = this.obtenerMaxIdEstado() + 1;
        Estado nuevoInicial = new Estado(offset);
        Estado nuevoFinal = new Estado(offset + 1);

        // Conectar nuevo inicial con el inicial actual y con el nuevo final (lambda)
        this.agregarTransicion(nuevoInicial, "_", this.estadoInicial);
        this.agregarTransicion(nuevoInicial, "_", nuevoFinal);

        // Conectar antiguos estados finales con el inicial y con el nuevo final (lambda)
        for (Estado ef : this.estadosFinales) {
            this.agregarTransicion(ef, "_", this.estadoInicial);
            this.agregarTransicion(ef, "_", nuevoFinal);
        }

        // Establecer el nuevo estado inicial
        this.setEstadoInicial(nuevoInicial);

        // Actualizar estados finales
        this.estadosFinales = new HashSet<>();
        this.agregarEstadoFinal(nuevoFinal);
    }

    // Une este AFN con otro (operación de unión)
    public void unirCon(AFN otro) {
        // Paso 1: Renombrar los estados del otro AFN
        int offset = this.obtenerMaxIdEstado() + 1;
        otro.renombrarEstadosConOffset(offset);

        // Paso 2: Crear nuevo estado inicial y final
        int nuevoIdInicial = otro.obtenerMaxIdEstado() + 1;
        Estado nuevoInicial = new Estado(nuevoIdInicial);
        Estado nuevoFinal = new Estado(nuevoIdInicial + 1);

        // Paso 3: Transiciones lambda desde el nuevo inicial a ambos AFNs
        this.agregarTransicion(nuevoInicial, "_", this.estadoInicial);
        this.agregarTransicion(nuevoInicial, "_", otro.estadoInicial);

        // Paso 4: Transiciones lambda desde los finales de ambos AFNs al nuevo final
        for (Estado ef : this.estadosFinales) {
            this.agregarTransicion(ef, "_", nuevoFinal);
        }
        for (Estado ef : otro.estadosFinales) {
            this.agregarTransicion(ef, "_", nuevoFinal);
        }

        // Paso 5: Unir componentes
        this.estados.addAll(otro.estados);
        this.transiciones.addAll(otro.transiciones);
        this.alfabeto.addAll(otro.alfabeto);

        // Paso 6: Establecer nuevos estados inicial y final
        this.setEstadoInicial(nuevoInicial);
        this.estadosFinales = new HashSet<>();
        this.agregarEstadoFinal(nuevoFinal);
    }
    // Construye un AFN básico para un solo símbolo
    public static AFN basico(char simbolo) {
        AFN afn = new AFN();
        Estado inicio = new Estado(0);
        Estado fin = new Estado(1);
        afn.setEstadoInicial(inicio);
        afn.agregarEstadoFinal(fin);
        afn.agregarTransicion(inicio, String.valueOf(simbolo), fin);
        return afn;
    }

    // Aplica la clausura de Kleene sobre un AFN y devuelve uno nuevo
    public static AFN clausuraKleene(AFN afnOriginal) {
        AFN afn = clonarAFN(afnOriginal);
        afn.aplicarClausuraKleene();
        return afn;
    }

    // Une dos AFNs y devuelve uno nuevo
    public static AFN union(AFN a1, AFN a2) {
        AFN copia1 = clonarAFN(a1);
        AFN copia2 = clonarAFN(a2);
        copia1.unirCon(copia2);
        return copia1;
    }

    // Concatena dos AFNs y devuelve uno nuevo
    public static AFN concatenacion(AFN a1, AFN a2) {
        AFN copia1 = clonarAFN(a1);
        AFN copia2 = clonarAFN(a2);
        copia1.concatenarAFN(copia2);
        return copia1;
    }

    // Clona un AFN (usado internamente para evitar mutaciones)
    private static AFN clonarAFN(AFN original) {
        AFN copia = new AFN();
        Map<Estado, Estado> mapa = new HashMap<>();

        for (Estado e : original.estados) {
            Estado nuevo = new Estado(e.id);
            nuevo.nombre = e.nombre;
            mapa.put(e, nuevo);
            copia.estados.add(nuevo);
        }

        copia.estadoInicial = mapa.get(original.estadoInicial);

        for (Estado ef : original.estadosFinales) {
            copia.estadosFinales.add(mapa.get(ef));
        }

        for (Transicion t : original.transiciones) {
            copia.transiciones.add(new Transicion(
                    mapa.get(t.desde),
                    t.simbolo,
                    mapa.get(t.hacia)
            ));
        }

        copia.alfabeto.addAll(original.alfabeto);

        return copia;
    }
    public AFD toAFD() {
        return new AFD(this); // usás el constructor de AFD que recibe un AFN
    }



}
