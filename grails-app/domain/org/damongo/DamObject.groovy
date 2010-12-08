package org.damongo

import  org.bson.types.ObjectId

class DamObject {

    ObjectId id
    String objectName

    static constraints = {
    }
    
	static mapWith = "mongo"
	
    static mapping = {
      collection "damObject"
      database "damongo"
      objectName index:true
    }
   
}
