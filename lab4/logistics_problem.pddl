
;; This is a small problem instance for the standard Logistics domain,
;; as defined in "logistic.pddl".

(define (problem C3_2)
  (:domain logistics)
  (:objects
   city1 city2 city3          ;; there are three cities,
   truck1 truck2 truck3       ;; one truck in each city,
   airplane1                  ;; only one airplane,
   office1 office2 office3    ;; offices are "non-airport" locations
   airport1 airport2 airport3 ;; airports, one per city,
   train1 train2	      
   train_station1 train_station2 train_station3
   packet1 packet2 packet3          ;; two packages to be delivered
   )
  (:init
   ;; Type declarations:
   (object packet1)
   (medium_size packet1)

   ;; all vehicles must be declared as both "vehicle" and their
   ;; appropriate subtype,
   (vehicle truck1) 
   (vehicle truck2) 
   (vehicle truck3) 
   (truck truck1) 
   (truck truck2) 
   (truck truck3) 
   (small_size truck1) 
   (medium_size truck2) 
   (large_size truck3)
   
   (vehicle airplane1)
   (airplane airplane1)
   (large_size airplane1)
   
   (vehicle train1) 
   (vehicle train2)
   (train train1) 
   (train train2)
   (medium_size train1) 
   (small_size train2)

   
   ;; Definitions of locations

   (city city1) 
   (city city2) 
   (city city3)

   (location office1) 
   (location office2) 
   (location office3)
   (loc office1 city1) 
   (loc office2 city2) 
   (loc office3 city3) 
   
   (location airport1) 
   (location airport2) 
   (location airport3)
   (airport airport1) 
   (airport airport2) 
   (airport airport3)
   (loc airport1 city1) 
   (loc airport2 city2) 
   (loc airport3 city3) 

   (location train_station1) 
   (location train_station2) 
   (location train_station3)
   (train_station train_station1) 
   (train_station train_station2) 
   (train_station train_station3)
   (connected_stations train_station1 train_station2) 
   (connected_stations train_station2 train_station1)
   (loc train_station1 city1) 
   (loc train_station2 city2) 
   (loc train_station3 city3)


   ;; The actual initial state of the problem, which specifies the
   ;; initial locations of all packages and all vehicles:
   (at packet1 train_station1)
   (at truck1 airport1)
   (at truck2 airport2)
   (at truck3 office3)
   (at airplane1 airport1)
   (at train1 train_station2)
   (at train2 train_station2)
   )

  ;; The goal is to have both packages delivered to their destinations:
  (:goal (and (at packet1 office3)))
  )
