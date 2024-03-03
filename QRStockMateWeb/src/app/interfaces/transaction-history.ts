export interface TransactionHistory {
    id: number;
    name: string;
    code: string;
    description: string;
    created: Date;
    operation: OperationHistory;
  }
  
  export enum OperationHistory {
    Add = "Add",
    Post = "Post",
    Put = "Put",
    Delete = "Delete"
  }
  