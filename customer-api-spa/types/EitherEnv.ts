export interface EitherEnv<Left, Right> {
  left: Left | null;
  right: Right | null;
}

export function makeLeft<Left, Right>(x: Left): EitherEnv<Left, Right> {
  return {
    left: x,
    right: null
  }
}

export function makeRight<Left, Right>(x: Right): EitherEnv<Left, Right> {
  return {
    left: null,
    right: x
  }
}

export function fold<Left, Right, Return>(x: EitherEnv<Left, Right>, fail: (l: Left) => Return, success: (r: Right) => Return): Return {
  if (x.right !== null){
    return success(x.right as Right)
  }
  if (x.left !== null){
    return fail(x.left as Left)
  }
  throw new Error('left and right values makePropertyFilterParams either is null')
}