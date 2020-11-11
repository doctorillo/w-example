import { RoleItem } from '../RoleItem'

export interface WorkspaceUI {
  userId: string;
  businessPartyId: string;
  businessParty: string;
  securities: RoleItem[];
}