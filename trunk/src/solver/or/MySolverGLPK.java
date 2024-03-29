package solver.or;

import java.util.HashMap;
import java.util.Map;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_iptcp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;

import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.ResultImpl;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverGLPK;
import net.sf.javailp.Term;
import net.sf.javailp.VarType;

public class MySolverGLPK extends SolverGLPK{
	public Result solve(Problem problem) {

		Map<Integer, Object> indexToVar = new HashMap<Integer, Object>();
		Map<Object, Integer> varToIndex = new HashMap<Object, Integer>();
		Map<Integer, Constraint> indexToCon = new HashMap<Integer, Constraint>();
		//Map<Constraint, Integer> conToIndex = new HashMap<Constraint, Integer>();

		int i = 1;
		for (Object variable : problem.getVariables()) {
			indexToVar.put(i, variable);
			varToIndex.put(variable, i);
			i++;
		}
		int k = 1;
		for (Constraint constraint : problem.getConstraints()) {
			indexToCon.put(k, constraint);
			//conToIndex.put(constraint, k);
			k++;
		}

		int ncon = problem.getConstraintsCount();
		int nvar = problem.getVariablesCount();

		glp_prob lp = null;

		lp = GLPK.glp_create_prob();

		try {
			GLPK.glp_set_prob_name(lp, "myProblem");

			{
				GLPK.glp_add_cols(lp, nvar);
				for (i = 1; i <= nvar; i++) {
					Object variable = indexToVar.get(i);

					VarType varType = problem.getVarType(variable);
					Number lowerBound = problem.getVarLowerBound(variable);
					Number upperBound = problem.getVarUpperBound(variable);

					final String name = variable.toString();
					final int kind;
					switch (varType) {
					case BOOL:
					case INT:
						kind = GLPKConstants.GLP_IV;
						break;
					default: // REAL
						kind = GLPKConstants.GLP_CV;
					}

					Double lb = null;
					Double ub = null;

					if (varType == VarType.BOOL) {
						lb = 0.0;
						ub = 1.0;
						if (lowerBound != null && lowerBound.doubleValue() > 0) {
							lb = 1.0;
						}
						if (upperBound != null && upperBound.doubleValue() < 1) {
							ub = 0.0;
						}
					} else {
						if (lowerBound != null) {
							lb = lowerBound.doubleValue();
						}
						if (upperBound != null) {
							ub = upperBound.doubleValue();
						}
					}

					final int bounds;
					if (lb != null && ub != null) {
						bounds = GLPKConstants.GLP_DB;
					} else if (lb != null) {
						bounds = GLPKConstants.GLP_LO;
					} else if (ub != null) {
						bounds = GLPKConstants.GLP_UP;
					} else {
						bounds = GLPKConstants.GLP_FR;
					}

					if (lb == null) {
						lb = 0.0;
					}
					if (ub == null) {
						ub = 0.0;
					}

					GLPK.glp_set_col_name(lp, i, name);
					GLPK.glp_set_col_kind(lp, i, kind);
					GLPK.glp_set_col_bnds(lp, i, bounds, lb, ub);
				}
			}

			{
				GLPK.glp_add_rows(lp, ncon);

				k = 1;
				for (k = 1; k <= ncon; k++) {
					Constraint constraint = indexToCon.get(k);

					Linear linear = constraint.getLhs();
					double rhs = constraint.getRhs().doubleValue();
					int size = linear.size();
					final String name = constraint.getName();

					SWIGTYPE_p_int vars = GLPK.new_intArray(size + 1);
					SWIGTYPE_p_double coeffs = GLPK.new_doubleArray(size + 1);

					int j = 1;
					for (Term term : linear) {
						Object variable = term.getVariable();
						int var = varToIndex.get(variable);
						double coeff = term.getCoefficient().doubleValue();

						GLPK.intArray_setitem(vars, j, var);
						GLPK.doubleArray_setitem(coeffs, j, coeff);
						j++;
					}

					final int comp;
					switch (constraint.getOperator()) {
					case LE:
						comp = GLPKConstants.GLP_UP;
						break;
					case GE:
						comp = GLPKConstants.GLP_LO;
						break;
					default: // EQ
						comp = GLPKConstants.GLP_FX;
					}

					GLPK.glp_set_row_name(lp, k, name);
					GLPK.glp_set_mat_row(lp, k, size, vars, coeffs);
					GLPK.glp_set_row_bnds(lp, k, comp, rhs, rhs);
					
				}
			}

			if (problem.getObjective() != null) {
				Linear objective = problem.getObjective();

				if (problem.getOptType() == OptType.MAX) {
					GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);
				} else {
					GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
				}

				GLPK.glp_set_obj_coef(lp, 0, 0);

				final Map<Object, Double> obj = new HashMap<Object, Double>();
				for (Term term : objective) {
					Object variable = term.getVariable();
					double coeff = term.getCoefficient().doubleValue();
					obj.put(variable, coeff);
				}

				for (i = 1; i <= nvar; i++) {
					Object variable = indexToVar.get(i);
					if (obj.containsKey(variable)) {
						double coeff = obj.get(variable);
						GLPK.glp_set_obj_coef(lp, i, coeff);
					} else {
						GLPK.glp_set_obj_coef(lp, i, 0);
					}
				}
			}

			Object timeout = parameters.get(Solver.TIMEOUT);
			Object verbose = parameters.get(Solver.VERBOSE);

			glp_smcp simplexParameters = new glp_smcp();
			glp_iocp integerParameters = new glp_iocp();
			glp_iptcp interiorParameters  = new glp_iptcp();
			
			
			GLPK.glp_init_smcp(simplexParameters);
			GLPK.glp_init_iocp(integerParameters);
			GLPK.glp_init_iptcp(interiorParameters);

			if (timeout != null) {
				int v = ((Number) timeout).intValue() *1000;

				integerParameters.setTm_lim(v);
				simplexParameters.setTm_lim(v);
				
			}

			if (verbose != null && verbose instanceof Number) {
				Number number = (Number) verbose;
				int value = number.intValue();
				final int msgLevel;

				switch (value) {
				case 0:
					msgLevel = GLPKConstants.GLP_MSG_OFF;
					GLPK.glp_term_out(GLPKConstants.GLP_OFF);
					break;
				case 1:
					msgLevel = GLPKConstants.GLP_MSG_ERR;
					break;
				case 2:
					msgLevel = GLPKConstants.GLP_MSG_ON;
					break;
				default: // >= 2
					msgLevel = GLPKConstants.GLP_MSG_ALL;
				}
				simplexParameters.setMsg_lev(msgLevel);
				integerParameters.setMsg_lev(msgLevel);
				interiorParameters.setMsg_lev(msgLevel);
			}
			
			for (Hook hook : hooks) {
				hook.call(lp, simplexParameters, integerParameters, varToIndex);
			}
			//GLPK.glp_interior(lp, interiorParameters);
			int ret=GLPK.glp_simplex(lp, simplexParameters);
			if(ret!=0) {
				return null;
			}
			int status = GLPK.glp_get_status(lp);

			if (status != GLPKConstants.GLP_OPT&&status != GLPKConstants.GLP_FEAS) {
				return null;
			}
			ret=GLPK.glp_intopt(lp, integerParameters);
			if(ret!=0) {
				return null;
			}
			
			status = GLPK.glp_mip_status(lp);

			if (status == GLPKConstants.GLP_OPT
					|| status == GLPKConstants.GLP_FEAS) {

				Result result;
				
				
				if (problem.getObjective() != null) {
					if(status == GLPKConstants.GLP_OPT) {
						result = new ResultImpl(problem.getObjective());
					}else {
						result = new ResultImpl(-999999);
					}
				} else {
					result = new ResultImpl();
				}
				
				
				// post-solve: LP relaxation with fixed integers
				Object postsolve = parameters.get(Solver.POSTSOLVE);
				if (postsolve != null && ((Number)postsolve).intValue() != 0 ) {
					boolean mip = false;
					for (i = 1; i<= GLPK.glp_get_num_cols(lp); i++) {
						int kind = GLPK.glp_get_col_kind(lp, i);
						if (kind == GLPKConstants.GLP_IV || kind == GLPKConstants.GLP_BV) {
							double x = GLPK.glp_mip_col_val(lp, i);
							GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_FX, x, x);
							mip = true;
						}
					}
		            if (mip) status = GLPK.glp_simplex(lp, simplexParameters);
				}

				for (i = 1; i <= nvar; i++) {
					Object variable = indexToVar.get(i);
					double primalValue = GLPK.glp_mip_col_val(lp, i);
					double dualValue = GLPK.glp_get_col_dual(lp, i);

					if (problem.getVarType(variable).isInt()) {
						int v = (int) Math.round(primalValue);
						result.putPrimalValue(variable, v);
					} else {
						result.putPrimalValue(variable, primalValue);
					}
					result.putDualValue(variable, dualValue);
				}

				for (i = 1; i <= ncon; i++) {
					Constraint constraint = indexToCon.get(i);
					double primalValue = GLPK.glp_get_row_prim(lp, i);
					double dualValue = GLPK.glp_get_row_dual(lp, i);
					result.putPrimalValue(constraint.getName(), primalValue);
					result.putDualValue(constraint.getName(), dualValue);
				}
				
				return result;
			} else {
				return null;
			}

		} finally {
			GLPK.glp_delete_prob(lp);
		}
	}
}
