# Parallel and Distributed Computing Projects

> Curricular Unit: [Parallel and Distributed Computing](https://sigarra.up.pt/feup/en/UCURR_GERAL.FICHA_UC_VIEW?pv_ocorrencia_id=541894)<br>
> Faculty: [FEUP](https://sigarra.up.pt/feup/en/web_page.Inicial)<br>
> Professor: [João Resende](https://sigarra.up.pt/feup/en/func_geral.formview?p_codigo=581903)<br>
> Authors: [Bruno Oliveira](https://github.com/Process-ing), [Mansur Mustafin](https://github.com/Mansur-Mustafin), [Rodrigo Silva](https://github.com/racoelhosilva)<br>
> Grades:<br>
> &emsp; First Project: 19.1/20<br>
> &emsp; Second Project: 19.0/20<br>

CPD Projects of group T13G12.

- **[Project 1](./assign1/src)**
- **[Project 2](./assign2/src)**

Group members:

1. Bruno Oliveira (up202208700@up.pt)
2. Mansur Mustafin (up202102355@up.pt)
3. Rodrigo Silva (up202205188@up.pt)

---

## Tips and Tricks (for anyone doing a similar project)

- **First Project:**
  - The code for the first project can be done really quickly, and it does not need to be very optimized, the results just need to make sense. The bulk of that project is the report: try to respond to everything asked in the project specification, using good charts and explanations.
  - They don't mention this, but the L3 cache miss analysis is (kind of) expected.
  - To avoid this from happening to anyone, apparently they also consider the appendix for the page limit (for which we were discounted), so yeah, don't use the trick of moving everything big to the appendix.
- **Second Project:**
  - Before doing the project, try to draw on paper the system architecture that you need and the message protocol you will use, making everything support what is asked by the project's description. This avoids many issues with the project design later on.
  - This probably won't happen to you, but we had the misfortune of not noticing the use of a blocking queue in the code (and this was the main reason we were penalized, without this we would probably get the maximum grade ;-;). So, make sure to see, right before submitting, if your code follows the project's restrictions like, in this case, the use of anything of the `java.util.concurrent` package.
