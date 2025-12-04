1) We will use only Token for serializing/deserializing
2) Add a hashMap in toke to maintin the properties of the specific TokenType, <String,String>
3) Door implementation
    - instead of BoardAreaConnection add AreaA and AreaB String
    - Set/get of this areas will call put, get of the base class
4) When we serialize we will create the token subclass using a factory
