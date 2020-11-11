import { ResultKind } from '../ResultKind'
import { LangItem } from '../LangItem'

export interface ActiveCmpProps {
  lang: LangItem;
  loadingStatus: ResultKind;
}