package net.mythrowaway.app.adapter

class DIContainer {
    companion object {
        private val containerMap:HashMap<Int, Any> = hashMapOf()
        fun <T:Any> register(key: Class<T>, instance: T) {
            containerMap[key.hashCode()] = instance
        }

        fun <T>resolve(key: Class<T>): T? {
            if(containerMap.containsKey(key.hashCode())) {
                return containerMap[key.hashCode()] as T
            }
            return null
        }
    }
}