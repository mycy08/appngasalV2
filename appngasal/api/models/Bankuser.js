/**
 * Bankuser.js
 *
 * @description :: TODO: You might write a short summary of how this model works and what it represents here.
 * @docs        :: http://sailsjs.org/documentation/concepts/models-and-orm/models
 */

module.exports = {

  attributes: {
    user_id:{
      type:'string'
    },
    bankname:{
      type:'string',
      required:true
    },
    bank_account_number:{
      type:'string',
      required:true
    },
    name:{
      type:'string',
      required:true
    }
  },
  connection:'appngasaldb'
};

