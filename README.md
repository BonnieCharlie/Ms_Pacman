Per lanciare una partia bisogna andare nel file Executor.java e decommentare se è commentata la riga di codice  exec.runGameTimed(new MyPacMan(RandomGhosts.class.getSimpleName()), new RandomGhosts(), visual);

Per provare il nostro controllore bisogna passare a exec.runGameTimed l'oggetto MyPacMan().
Inoltre per poter avviare il nostro controllore bisogna inserire il tipo di controllore dei Ghosts. 

In particolare passare RandomGhosts.class.getSimpleName() se si vuole utilizzare il controllore contro i RandomGhosts e Legacy.class.getSimpleName() se si vuole giocare contro il controllore Legacy.
E' possibile inoltre passare un ulteriore parametro che rappresenta la profondità dell'albero di ricerca. Se non la si setta di default è 10 per i Random e 30 per i Legacy.

In src > pacman > entries > pacman > MyPacMan è presente il codice del controllore.
In src > pacman > entries > pacman > Utils vi sono le meta-euristiche.

In src > pacman > game sono presenti i file utilizzati per effettuare i test:
- main effettua un test su 50 partite calcolando score medio, max e min.
- DepthBound effettua test modificando la depth, ogni depth viene provata su 100 partite.
- TTest è il file utilizzato per raccogliere i dati utilizzati per effettuare il TTest.