import { User } from "./user";

export interface TransactionHistory {
  id: number;
  name: string;
  code: string;
  description: string;
  created: Date;
  operation: OperationHistory | Number;
}

export enum OperationHistory {
  Add = "Add",
  Post = "Post",
  Put = "Put",
  Delete = "Delete"
}
// Función para obtener el índice de un valor en el enum
export function getIndexFromOperation(operation: OperationHistory): Number {
  const keys = Object.keys(OperationHistory).filter(key => OperationHistory[key as keyof typeof OperationHistory] === operation);
  if (keys.length === 1) {
    return parseInt(keys[0]);
  }
  return 0;
}

export function me(): User | null{
  var stringU = sessionStorage.getItem('me')
  var me:User
  if (stringU) {
    me  = JSON.parse(stringU)
    return me;
  }

  return null;
}

export function token(): string{
  var stringT = sessionStorage.getItem('token')
  var token:string
  if (stringT) {
    token  = JSON.parse(stringT)
    return token;
  }

  return "";
}
