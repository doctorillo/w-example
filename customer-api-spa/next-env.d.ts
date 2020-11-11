/// <reference types="next" />
/// <reference types="next/types/global" />
/*declare module '*.css' {
  const content: string;
  export default content;
}*/
declare module '*.css' {
  const styles: any;
  export = styles;
}
declare module '*.svg' {
  type attrs = {
    width: string;
    height: string;
    fill: string;
  };
  const resource: string & attrs;
  export = resource;
}