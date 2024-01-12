import { IPlantage } from 'app/shared/model/plantage.model';
import { IFerme } from 'app/shared/model/ferme.model';
import { IPlante } from 'app/shared/model/plante.model';

export interface IParcelle {
  id?: number;
  libelle?: string | null;
  photo?: string | null;
  plantages?: IPlantage[] | null;
  ferme?: IFerme | null;
  plantes?: IPlante[] | null;
  fermeLibelle?: IFerme | null;
}

export const defaultValue: Readonly<IParcelle> = {};
