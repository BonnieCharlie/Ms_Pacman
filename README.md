Per lanciare una partita bisogna andare nel file Executor.java e decommentare se è commentata la riga di codice  exec.runGameTimed(new MyPacMan(RandomGhosts.class.getSimpleName()), new RandomGhosts(), visual);

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


ENGLISH

Run Executor.java to lauch the game. Toggle the comment "exec.runGameTimed(new MyPacMan(RandomGhosts.class.getSimpleName()), new RandomGhosts(), visual);" if it is commented.

To try our controller you have to pass the object MyPacMan() to exec.runGameTimed. Morover you have to insert inside this function what type of controller do you want to use: RandomGhosts.class.getSimpleName() for the Random Ghosts Controller and Legacy.class.getSimpleName() for Legacy one. It is possible to add an additional parameter that represents the search tree depth.  If you don't set this parameter it will be 10 as default for Random Ghosts and 30 for Legacy Ghosts.

The Controller code is in src > pacman > entries > pacman > MyPacMan.
The Metaheuristics are in src > pacman > entries > pacman > Utils.
The file used for the tests are in src > pacman > game:
- main makes a test on 50 games computing max, avarage and min scores.
- depthBound makes a test updating the depth; every depth is trying on 100 games.
- TTest is the file used to collect the date used for the T tests.
