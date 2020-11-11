import { Client} from './Client'

export interface Group {
  id: string;
  clients: Client[];
  position: number;
}