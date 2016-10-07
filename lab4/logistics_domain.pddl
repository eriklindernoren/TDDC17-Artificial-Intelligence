
;; This is a plain STRIPS formulation of the standard Logistics domain.

;; In this domain, there are six different types of objects: "object"
;; (the packages to be transported), "truck", "airplane" and their
;; common supertype "vehicle", "location" and the subtype "airport",
;; and finally "city". Types are defined by static (in the sense that
;; there are no operators that change their truth value) unary predicates.
;; The types of objects in a problem instance must be defined by including
;; the appropriate typing predicates in the initial state.

;; A binary static predicate called "loc" describes the topology of the
;; problem instance: "(loc ?l ?c)" is true iff the location ?l is in city
;; ?c.

(define (domain logistics)
  (:requirements :strips)
  (:predicates

   ;; Static predicates:
   (object ?o) (truck ?t) (airplane ?p) (vehicle ?v)
   (location ?l) (airport ?a) (city ?c) (loc ?l ?c) 
   
   (train ?tr) 				;; New vehicle train
   (train_station ?ts) 			;; Train station
   (connected_stations ?ts1 ?ts2)	;; If station ?ts1 is connected to ?ts2
   
   (small_size ?ss) 			;; Vehicle ?ss of small size
   (medium_size ?ms) 			;; Vehicle ?ms of medium size
   (large_size ?ls)			;; Vehicle ?ls of large size

   ;; Non-static predicates:
   (at ?x ?l) 	;; ?x (package or vehicle) is at location ?l
   (in ?p ?v) 	;; package ?p is in vehicle ?v
   (loaded ?v)	;; If a package has been loaded on vehicle ?v
   )

  ;; Actions for loading and unloading packages.
  ;; By declaring all trucks and airplanes to be also "vehicle", we
  ;; can use the same load/unload operator for both (otherwise we
  ;; would need one for each subtype of vehicle).
  ;; ADDITIONS: Vehicles and objects can be small, medium or large. Small vehiclas
  ;; can only load small objects, medium vehicles can load small and medium objects and
  ;; large vehicles can load small, medium and large objects.
  (:action load
    :parameters (?o ?v ?l)
    :precondition (and (object ?o) (vehicle ?v) (location ?l)
		       (at ?v ?l) (at ?o ?l) (not (loaded ?v))
		   (or (and (small_size ?v) (small_size ?o))
	 	       (and (medium_size ?v) (or (small_size ?o) (medium_size ?o)))
		       (and (large_size ?v) (or (small_size ?o) (medium_size ?o) (large_size ?o)))
		   )
		)
    :effect (and (loaded ?v) (in ?o ?v) (not (at ?o ?l))))

  (:action unload
    :parameters (?o ?v ?l)
    :precondition (and (object ?o) (vehicle ?v) (location ?l)
		       (at ?v ?l) (in ?o ?v) (loaded ?v))
    :effect (and (not (loaded ?v)) (at ?o ?l) (not (in ?o ?v))))

  ;; Drive a truck between two locations in the same city.
  ;; By declaring all locations, including airports, to be of type
  ;; "location", we can use only one driving operator (otherwise,
  ;; we would again need one for each case, i.e. one for from-location-
  ;; to-airport, one for from-location-to-location, etc. Very
  ;; unnecessay).
  (:action drive
    :parameters (?t ?l1 ?l2 ?c)
    :precondition (and (truck ?t) (location ?l1) (location ?l2) (city ?c)
		       (at ?t ?l1) (loc ?l1 ?c) (loc ?l2 ?c))
    :effect (and (at ?t ?l2) (not (at ?t ?l1))))

  ;; Fly an airplane between two airports.
  (:action fly
    :parameters (?p ?a1 ?a2)
    :precondition (and (airplane ?p) (airport ?a1) (airport ?a2)
		       (at ?p ?a1))
    :effect (and (at ?p ?a2) (not (at ?p ?a1))))

  ;; Take train between two train stations
  ;; The train can only travel between two stations that are connected
  (:action take_train
    :parameters (?tr ?ts1 ?ts2)
    :precondition (and 
			(train ?tr) 
			(train_station ?ts1) 
			(train_station ?ts2) 
			(connected_stations ?ts1 ?ts2)
           		(at ?tr ?ts1)
		)
    :effect (and (at ?tr ?ts2) (not (at ?tr ?ts1))))
  )
