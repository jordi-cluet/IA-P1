import java.util.*;
import IA.DistFS.Servers;
import IA.DistFS.Requests;

public class Estado {

    //Atributos    
    static int npet;
    static int nserv;
    static Servers servidores;
    static Requests peticiones;
    int[] tiempo_servidores;
    int[] asignaciones;

    public Estado(int nusu, int npet, int nserv, int nrep, int seed1, int seed2) throws Servers.WrongParametersException {
        this.npet = npet;
        this.nserv = nserv;
        peticiones = new Requests(nusu, npet, seed1);  //usuarios, peticiones, seed
        servidores = new Servers(nserv, nrep, seed2); //servidores, replicas, seed
        asignaciones = new int[npet];
        for (int i = 0; i < npet; ++i) {
            int user = peticiones.getRequest(i)[0];
            int file = peticiones.getRequest(i)[1];

            Iterator<Integer> it = servidores.fileLocations(file).iterator();

            System.out.println("La peticion " + i + " tiene como usuario "+user+" y fichero " +file); 
            System.out.println("El fichero mencionado se encuentra en los servidores :");
            while(it.hasNext()){
                int serv = it.next();
                System.out.println("  " + serv + " que tardaria "+ servidores.tranmissionTime(serv,user) + "ms");
            }
            //System.out.println(asignaciones.get(i));
            //System.out.println();
        }
    }
    
    public Estado(Estado estat){
        asignaciones = estat.asignaciones.clone();
    }

	//Métodos
    public boolean isGoalState() {
        return false;
    }

    public void generaSolInicial() {
        
        for (int i=0; i<npet; ++i) {
            int fileID = peticiones.getRequest(i)[1];
            Iterator<Integer> it = servidores.fileLocations(fileID).iterator(); 
            asignaciones[i] = it.next();
        }
        imprimir_asignaciones();
    }

    public void imprimir_asignaciones() {
        int tiempo_total = 0;
        for (int i=0; i<asignaciones.length; ++i){
            int serv = asignaciones[i];
            System.out.print(i + " -> " + serv + "   ");
            tiempo_total += servidores.tranmissionTime(serv,peticiones.getRequest(i)[0]);
        }
        System.out.print("Tiempo: "+ calcular_tiempo_servidores());
        System.out.println("\n");
    }
    
    private int calcular_tiempo_servidores(){

        tiempo_servidores = new int[nserv];
        int max = 0;
        int suma = 0;

        for (int i=0; i<npet; ++i) {
            int userID = peticiones.getRequest(i)[0];
            int serverID = asignaciones[i];
            int tiempo = servidores.tranmissionTime(serverID, userID);
            tiempo_servidores[serverID] = tiempo_servidores[serverID] + tiempo;
            if (tiempo_servidores[serverID] > max)
                max = tiempo_servidores[serverID];
            suma += tiempo;
        }
        return max;
    }
    
    public double getHeuristicValue() {
        return (double) calcular_tiempo_servidores();
    }
    
    public boolean moverPeticion(int pet, int serv) {
        if (asignaciones[pet] == serv) return false;
        else {
            asignaciones[pet]= serv;
            return true;
        }
    }

    // public boolean intercambiarPeticiones(int pet1, int pet2) {
    //     if (asignaciones.get(pet1) == asignaciones.get(pet2)) return false;
    //     //if (pet1 no es pot assignar al servidor de pet2) return false;
    //     //if (pet2 no es pot assignar al servidor de pet1) return false;
    //     else {
    //         asignaciones.add(pet1, /*servidor de pet2*/);
    //         asignaciones.add(pet2, /*servidor de pet1*/);
    //         return true;
    //     }
    // }
}
