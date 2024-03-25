export interface Message {
    id: number;
    code: string;
    senderContactId: number;
    receiverContactId: number;
    content: string;
    sentDate: Date;
    type: TypeFile;
  }
  export enum TypeFile {
    Text,
    Audio,
    File,
    Image
  }
  